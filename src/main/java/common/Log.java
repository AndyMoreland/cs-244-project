package common;

import PBFT.*;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.istack.internal.Nullable;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import statemachine.InvalidStateMachineOperationException;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by andrew on 11/27/14.
 */
public class Log<T> {
    private Logger LOG = LogManager.getLogger(Log.class);

    private ReadWriteLock logLock = new ReentrantReadWriteLock(true);
    private List<LogListener<T>> listeners = Lists.newArrayList();

    // {SequenceNumber => { ViewStamp => Transaction }}
    private Map<Integer, Map<Viewstamp, Transaction<T>>> tentativeLogEntries = Maps.newConcurrentMap();
    private Map<Integer, Transaction<T>> committedLogEntries = Maps.newConcurrentMap();

    private Map<Viewstamp, Transaction<T>> transactions = Maps.newConcurrentMap();
    private Map<MultiKey<Viewstamp, Digest>, Set<PrepareMessage>> prepareMessages = Maps.newConcurrentMap();

    private Map<MultiKey<Viewstamp, Digest>, Set<CommitMessage>> commitMessages = Maps.newConcurrentMap();
    private int lastApplied = -1;

    private Map<Integer, Transaction<T>> unappliedLogEntries = Maps.newConcurrentMap();
    private Set<Transaction<T>> failedTransactions = Sets.newHashSet();

    // for checkpointing and view changes
    // view # =>
    private Map<Integer, Set<ViewChangeMessage>> viewChangeMessages = Maps.newHashMap(); // this should include your own messages
    // seqno =>
    private Map<Integer, Set<CheckpointMessage>> checkpointMessages = Maps.newHashMap();
    // TODO (Susan): is it possible that there are multiple preprepares for the same seqno? i.e. from different leaders,
    // where the first one is the one I'm actually interested in?
    private Map<Integer, PrePrepareMessage> prePrepareMessageMap = Maps.newConcurrentMap(); // for not committed seqnos
    private int lastStableCheckpoint = 0;

    public Log() {

    }

    public Log(List<LogListener<T>> listeners) {
        this.listeners = listeners;
    }

    public void addEntry(Transaction<T> value, PrePrepareMessage message) throws IllegalLogEntryException {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();

        try {

            int sequenceNumber = value.getViewstamp().getSequenceNumber();
            if (tentativeLogEntries.containsKey(sequenceNumber)) {
                Map<Viewstamp, Transaction<T>> sequenceNumberTransactions = tentativeLogEntries.get(value.getViewstamp().getSequenceNumber());
                if (sequenceNumberTransactions.containsKey(value.getViewstamp()) && sequenceNumberTransactions.get(value.getViewstamp()) != value) {
                    throw new IllegalLogEntryException();
                }
                tentativeLogEntries.get(sequenceNumber).put(value.getViewstamp(), value);
            } else {
                Map<Viewstamp, Transaction<T>> map = Maps.newHashMap();
                map.put(value.getViewstamp(), value);
                tentativeLogEntries.put(sequenceNumber, map);
            }

            transactions.put(value.getViewstamp(), value);
            prePrepareMessageMap.put(value.getViewstamp().getSequenceNumber(), message);
        } finally {
            writeLock.unlock();
        }
    }

    public int getLastApplied() { return lastApplied; }

    public int getNextSequenceNumber() { return lastApplied + 1; } // FIXME: (andy or leo) this is incorrect. It should return the last commited + 1 but we don't track that.

    @Nullable
    public Transaction<T> getTransaction(Viewstamp viewstamp) {
        Lock readLock = logLock.readLock();
        readLock.lock();
        Transaction<T> t = transactions.get(viewstamp);
        readLock.unlock();

        return t;
    }

    @Nullable Transaction<T> getTransaction(int sequenceNo){
        Lock readLock = logLock.readLock();
        readLock.lock();
        Transaction<T> bestTr = null;
        for(Map.Entry<Viewstamp, Transaction<T>> entry : transactions.entrySet()){
            if(entry.getKey().getSequenceNumber() != sequenceNo) continue;
            if(bestTr == null || bestTr.getViewstamp().getViewId() < entry.getKey().getViewId()) bestTr = entry.getValue();
        }
        return bestTr;
    }

    public Collection<Transaction<T>> getTentativeEntries(int index) {
        Lock readLock = logLock.readLock();
        readLock.lock();
        Map<Viewstamp, Transaction<T>> val = tentativeLogEntries.get(index);
        readLock.unlock();

        if (val != null) {
            return val.values();
        } else {
            return Sets.newHashSet();
        }
    }

    public Optional<Transaction<T>> getEntry(int index) {
        Lock readLock = logLock.readLock();
        readLock.lock();
        Transaction<T> val = committedLogEntries.get(index);
        readLock.unlock();

        return Optional.of(val);
    }

    public void commitEntry(Viewstamp id) {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();

        assert (tentativeLogEntries.containsKey(id.getSequenceNumber()));

        Transaction<T> entry = transactions.get(id);
        tentativeLogEntries.remove(id.getSequenceNumber());
        prePrepareMessageMap.remove(id.getSequenceNumber());
        committedLogEntries.put(entry.getViewstamp().getSequenceNumber(), entry);
        unappliedLogEntries.put(entry.getViewstamp().getSequenceNumber(), entry);
        entry.commit();

        LOG.info("COMMITED ENTRY: " + id.toString());
        LOG.info("Sequence number: " + id.getSequenceNumber() + " " + " and last applied: " + lastApplied);

        if (id.getSequenceNumber() == lastApplied + 1) {
            flushUnappliedEntries(writeLock);
        }

        writeLock.unlock();
    }

    /* Assumes that a writelock is held. */
    private void flushUnappliedEntries(Lock writeLock) {
        List<Transaction<T>> unnotifiedLogEntries = Lists.newArrayList();

        while (unappliedLogEntries.get(lastApplied + 1) != null) {
            Transaction<T> transaction = unappliedLogEntries.get(lastApplied + 1);
            unappliedLogEntries.remove(lastApplied + 1);
            unnotifiedLogEntries.add(transaction);
            lastApplied++;
        }


        writeLock.unlock();
        for (Transaction<T> transaction: unnotifiedLogEntries) {

            boolean failed = false;

            for (LogListener<T> listener : listeners) {
                try {
                    listener.notifyOnCommit(transaction);
                } catch (InvalidStateMachineOperationException e) {
                    failed = true;
                    e.printStackTrace();
                } catch (Exception e) {
                    LOG.warn("Failed to apply operation.");
                    e.printStackTrace();
                }
            }

            writeLock.lock();

            if (failed) {
                LOG.warn("Invalid state machine operation caught");
                failedTransactions.add(transaction);
                /* FIXME: We need to handle this case. */
            }

            writeLock.unlock();
        }

        writeLock.lock();
    }

    public void addPrepareMessage(PrepareMessage message) {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();
        MultiKey<Viewstamp, Digest> key = MultiKey.newKey(message.getViewstamp(), new Digest(message.getTransactionDigest()));
        if(!prepareMessages.containsKey(key)) prepareMessages.put(key, Sets.<PrepareMessage>newHashSet());
        prepareMessages.get(key).add(message);
        writeLock.unlock();
    }

    public void addCommitMessage(CommitMessage message) {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();
        MultiKey<Viewstamp, Digest> key = MultiKey.newKey(message.getViewstamp(), new Digest(message.getTransactionDigest()));
        if(!commitMessages.containsKey(key)) commitMessages.put(key, Sets.<CommitMessage>newHashSet());
        commitMessages.get(key).add(message);
        writeLock.unlock();
    }

    public void addViewChangeMessage(ViewChangeMessage message) {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();
        int newViewID = message.getNewViewID();
        if (viewChangeMessages.containsKey(newViewID)) {
            viewChangeMessages.get(newViewID).add(message);
        } else {
            viewChangeMessages.put(newViewID, Sets.newHashSet(message));
        }
        writeLock.unlock();
    }

    public void markAsPrepared(Viewstamp id){
        Lock writeLock = logLock.writeLock();
        writeLock.lock();
        transactions.get(id).prepare();
        writeLock.unlock();
    }

    public void markViewChangeCompleted(int viewID) {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();
        // clear old entries for old views out from viewChangeMessages
        for (Iterator<Map.Entry<Integer, Set<ViewChangeMessage>>> it
                     = viewChangeMessages.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Set<ViewChangeMessage>> entry = it.next();
            if (entry.getKey().compareTo(viewID) <= 0) {
                it.remove();
            }
        }
        writeLock.unlock();
    }

    public int getLastStableCheckpoint() {
        Lock readLock = logLock.readLock();
        readLock.lock();
        int checkpoint = lastStableCheckpoint;
        readLock.unlock();
        return checkpoint;
    }

    public int getPreparesCheckpointProofAndLastStableCheckpoint(
            Map<PrePrepareMessage, Set<PrepareMessage>> prePreparesAndProof,
            Set<CheckpointMessage> checkPointProof) {
        Lock readLock = logLock.readLock();
        readLock.lock();
        for (Map.Entry<Integer, PrePrepareMessage> prePrepareMessageEntry : prePrepareMessageMap.entrySet()) {
            MultiKey<Viewstamp, Digest> vdk = MultiKey.newKey(
                    prePrepareMessageEntry.getValue().getViewstamp(), new Digest(prePrepareMessageEntry.getValue().getTransactionDigest()));
            if (prePrepareMessageEntry.getKey() > lastStableCheckpoint &&
                    prepareMessages.get(vdk) != null) {
                prePreparesAndProof.put(prePrepareMessageEntry.getValue(),prepareMessages.get(vdk));
            }
        }
        checkPointProof.addAll(checkpointMessages.get(lastStableCheckpoint));
        int checkpoint = lastStableCheckpoint;
        readLock.unlock();
        return checkpoint;
    }

    public void addCheckpointMessage(CheckpointMessage message, int quorumSize) {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();
        if (checkpointMessages.containsKey(message.getSequenceNumber())) {
            checkpointMessages.get(message.getSequenceNumber()).add(message);
        } else {
            checkpointMessages.put(message.getSequenceNumber(), Sets.newHashSet(message));
        }

        // possibly make this a new stable checkpoint and clear all the old checkpoint information
        if (checkpointMessages.get(message.getSequenceNumber()).size() >= quorumSize) {
            lastStableCheckpoint = message.getSequenceNumber();
            LOG.trace("Last stable checkpoint is " + lastStableCheckpoint);
            for (Iterator<Map.Entry<Integer, Set<CheckpointMessage>>> it = checkpointMessages.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Integer, Set<CheckpointMessage>> entry = it.next();
                if (entry.getKey() < lastStableCheckpoint) { // keep around the proof for the current checkpoint though
                    it.remove();
                }
            }
        }
        writeLock.unlock();
    }

    public boolean readyToPrepare(Viewstamp viewstamp, Digest mtd, int quorumSize) {
        Lock readLock = logLock.readLock();
        readLock.lock();
        Transaction<T> transaction = transactions.get(viewstamp);
        boolean quorum;

        // Haven't seen this viewstamp, or have already processed, or haven't prepared this request
        if(transaction == null || transaction.isPrepared() || !CryptoUtil.computeDigest(transaction).equals(mtd)){
            quorum = false;
        } else {
            Set<PrepareMessage> previouslyReceivedPrepareMessages = prepareMessages.get(MultiKey.newKey(viewstamp, mtd));
            quorum = (previouslyReceivedPrepareMessages == null ? 0 : previouslyReceivedPrepareMessages.size()) >= quorumSize - 1; // -1 for own log entry
        }
        readLock.unlock();
        return quorum;
    }

    public boolean readyToSendNewView(int newViewID, int quorumSize) {
        boolean ready;
        Lock readLock = logLock.readLock();
        readLock.lock();
        ready = viewChangeMessages.get(newViewID).size() >= quorumSize;
        readLock.unlock();
        return ready;
    }

    public Set<ViewChangeMessage> getViewChangeMessages(int viewID) {
        Set<ViewChangeMessage> viewChangeMessages;
        Lock readLock = logLock.readLock();
        readLock.lock();
        viewChangeMessages = this.viewChangeMessages.get(viewID);
        readLock.unlock();
        return viewChangeMessages;
    }

    public boolean readyToCommit(Viewstamp viewstamp, Digest mtd, int quorumSize) {
        Lock readLock = logLock.readLock();
        readLock.lock();
        Transaction<T> transaction = transactions.get(viewstamp);
        boolean quorum;
        if(transaction == null || !transaction.isPrepared() || transaction.isCommitted()){
            quorum = false; // Not (pre)prepared yet or already committed => ignore
        } else {
            Set<CommitMessage> commitsReceived = commitMessages.get(MultiKey.newKey(viewstamp, mtd));
            quorum = commitsReceived != null && commitsReceived.size() >= quorumSize - 1;
        }

        readLock.unlock();
        return quorum;
    }

    @Override
    public String toString() {
        return "Log{" +
                "tentativeLogEntries=" + tentativeLogEntries +
                ", committedLogEntries=" + committedLogEntries +
                ", transactions=" + transactions +
                ", prepareMessages=" + prepareMessages +
                ", commitMessages=" + commitMessages +
                ", logLock=" + logLock +
                ", lastApplied=" + lastApplied +
                '}';
    }

    public void addListener(LogListener<T> logListener) {
        LOG.info("Registered listener: " + logListener);
        listeners.add(logListener);
    }
}
