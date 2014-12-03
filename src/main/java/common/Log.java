package common;

import PBFT.CommitMessage;
import PBFT.PrepareMessage;
import PBFT.Viewstamp;
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

    private ReadWriteLock logLock = new ReentrantReadWriteLock();
    private List<LogListener<T>> listeners = Lists.newArrayList();

    // {SequenceNumber => { ViewStamp => Transaction }}
    private Map<Integer, Map<Viewstamp, Transaction<T>>> tentativeLogEntries = Maps.newConcurrentMap();
    private Map<Integer, Transaction<T>> committedLogEntries = Maps.newConcurrentMap();

    private Map<Viewstamp, Transaction<T>> transactions = Maps.newConcurrentMap();
    private Map<MultiKey<Viewstamp, TransactionDigest>, Set<PrepareMessage>> prepareMessages = Maps.newConcurrentMap();

    private Map<MultiKey<Viewstamp, TransactionDigest>, Set<CommitMessage>> commitMessages = Maps.newConcurrentMap();
    private int lastCommited = -1;

    private Map<Integer, Transaction<T>> unappliedLogEntries = Maps.newConcurrentMap();
    private Set<Transaction<T>> failedTransactions = Sets.newHashSet();

    public Log() {

    }

    public Log(List<LogListener<T>> listeners) {
        this.listeners = listeners;
    }

    public void addEntry(Transaction<T> value) throws IllegalLogEntryException {
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

        } finally {
            writeLock.unlock();
        }
    }

    public int getLastCommited() { return lastCommited; }

    @Nullable
    public Transaction<T> getTransaction(Viewstamp viewstamp) {
        Lock readLock = logLock.readLock();
        readLock.lock();
        Transaction<T> t = transactions.get(viewstamp);
        readLock.unlock();

        return t;
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
        committedLogEntries.put(entry.getViewstamp().getSequenceNumber(), entry);
        unappliedLogEntries.put(entry.getViewstamp().getSequenceNumber(), entry);
        entry.commit();

        LOG.info("COMMITED ENTRY: " + id.toString());
        LOG.info(id.getSequenceNumber() + " " + lastCommited);

        if (id.getSequenceNumber() == lastCommited + 1) {
            lastCommited++;
            flushUnappliedEntries();
        }

        writeLock.unlock();
    }

    private void flushUnappliedEntries() {
        while (unappliedLogEntries.get(lastCommited) != null) {
            Transaction<T> transaction = unappliedLogEntries.get(lastCommited);
            boolean failed = false;
            for (LogListener<T> listener : listeners) {
                try {
                    listener.notifyOnCommit(transaction);
                } catch (InvalidStateMachineOperationException e) {
                    failed = true;
                } catch(Exception e) {
                    LOG.warn("Failed to apply operation.");
                    e.printStackTrace();
                }
            }

            if (failed) {
                LOG.warn("Invalid state machine operation caught");
                failedTransactions.add(transaction);
                /* FIXME: We need to handle this case. */
            }

            unappliedLogEntries.remove(lastCommited);
            lastCommited++;
        }
    }

    public void addPrepareMessage(PrepareMessage message) {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();
        MultiKey<Viewstamp, TransactionDigest> key = MultiKey.newKey(message.getViewstamp(), new TransactionDigest(message.getTransactionDigest()));
        if(!prepareMessages.containsKey(key)) prepareMessages.put(key, Sets.<PrepareMessage>newHashSet());
        prepareMessages.get(key).add(message);
        writeLock.unlock();
    }

    public void addCommitMessage(CommitMessage message) {
        Lock writeLock = logLock.writeLock();
        writeLock.lock();
        MultiKey<Viewstamp, TransactionDigest> key = MultiKey.newKey(message.getViewstamp(), new TransactionDigest(message.getTransactionDigest()));
        if(!commitMessages.containsKey(key)) commitMessages.put(key, Sets.<CommitMessage>newHashSet());
        commitMessages.get(key).add(message);
        writeLock.unlock();
    }

    public void markAsPrepared(Viewstamp id){
        transactions.get(id).prepare();
    }

    public boolean readyToPrepare(Viewstamp viewstamp, TransactionDigest mtd, int quorumSize){
        Lock readLock = logLock.readLock();
        readLock.lock();
        Transaction<T> transaction = transactions.get(viewstamp);
        boolean quorum;

        // Haven't seen this viewstamp, or have already processed, or haven't prepared this request
        if(transaction == null || transaction.isPrepared() || !CryptoUtil.computeTransactionDigest(transaction).equals(mtd)){
            quorum = false;
        } else {
            Set<PrepareMessage> previouslyReceivedPrepareMessages = prepareMessages.get(MultiKey.newKey(viewstamp, mtd));
            quorum = (previouslyReceivedPrepareMessages == null ? 0 : previouslyReceivedPrepareMessages.size()) >= quorumSize - 1; // -1 for own log entry
        }
        readLock.unlock();
        return quorum;
    }

    public boolean readyToCommit(Viewstamp viewstamp, TransactionDigest mtd, int quorumSize) {
        Lock readLock = logLock.readLock();
        readLock.lock();
        Transaction<T> transaction = transactions.get(viewstamp);
        boolean quorum;
        if(transaction == null || !transaction.isPrepared() || transaction.isCommitted()){
            quorum = false; // Not (pre)prepared yet or already committed => ignore
        } else {
            quorum = commitMessages.get(MultiKey.newKey(viewstamp, mtd)).size() >= quorumSize - 1;
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
                ", lastCommited=" + lastCommited +
                '}';
    }

    public void addListener(LogListener<T> logListener) {
        LOG.info("Registered listener: " + logListener);
        listeners.add(logListener);
    }
}
