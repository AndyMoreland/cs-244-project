package server;

import PBFT.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.istack.internal.Nullable;
import common.*;
import config.GroupConfigProvider;
import config.GroupMember;
import gameengine.ChineseCheckersState;
import gameengine.operations.NoOp;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import statemachine.Operation;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static PBFT.PBFTCohort.Iface;

/**
 * Created by andrew on 11/27/14.
 */
public class PBFTCohortHandler implements Iface {
    private static Logger LOG = LogManager.getLogger(PBFTCohortHandler.class);
    private final Log<Operation<ChineseCheckersState>> log;
    private GroupConfigProvider<PBFTCohort.Client> configProvider;
    private final GroupMember<PBFTCohort.Client> thisCohort;
    private Map<Integer, List<ViewChangeMessage>> viewChangeMessages; // this should include your own messages
    private int replicaID;
    private final ExecutorService pool;

    private static final int POOL_SIZE = 10;
    private static final int LAST_CHECKPOINT = 0; // set to 0 for now; no checkpointing
    private static final int MIN_SEQ_NO = 0;
    private static final int MIN_VIEW_ID = 0;
    private static final byte[] NO_OP_TRANSACTION_DIGEST = CryptoUtil.computeTransactionDigest(
            new common.Transaction(null, -1, new NoOp(), 0)).getBytes();

    private static final int CHECKPOINT_INTERVAL = 100;


    public PBFTCohortHandler(GroupConfigProvider<PBFTCohort.Client> configProvider, int replicaID, GroupMember<PBFTCohort.Client> thisCohort, LogListener toNotify) {
        this.configProvider = configProvider;
        viewChangeMessages = Maps.newHashMap();
        this.replicaID = replicaID;
        pool = Executors.newFixedThreadPool(POOL_SIZE);
        this.log = new Log<Operation<ChineseCheckersState>>();
        this.thisCohort = thisCohort;

        this.log.addListener(toNotify);

        LOG.info("Starting handler!");
    }

    @Override
    public void clientMessage(ClientMessage message) throws TException {
        LOG.info("Got client message");
        if(this.configProvider.getLeader().getReplicaID() == this.replicaID) return;
        LOG.info("I'm the leader");
        if (!this.configProvider.getGroupMember(message.getReplicaId()).verifySignature(message, message.getMessageSignature())) return;
        LOG.info("validated signature! multicasting prePrepares...");

        TTransaction transaction = new TTransaction();
        transaction.viewstamp = new Viewstamp(log.getNextSequenceNumber(), configProvider.getViewID()); // TODO this sequence number is not correct
        transaction.replicaId = message.getReplicaId();
        transaction.operation = message.operation;


        for (final GroupMember<PBFTCohort.Client> member : configProvider.getOtherGroupMembers()) {
            final PrePrepareMessage prePrepareMessage = new PrePrepareMessage();
            prePrepareMessage.viewstamp = transaction.getViewstamp();
            prePrepareMessage.replicaId = thisCohort.getReplicaID();
            prePrepareMessage.transactionDigest = ByteBuffer.wrap(
                    CryptoUtil.computeTransactionDigest(Transaction.getTransactionForPBFTTransaction(transaction)).getBytes()
            );
            prePrepareMessage.messageSignature = ByteBuffer.wrap(CryptoUtil.computeMessageSignature(prePrepareMessage, thisCohort.getPrivateKey()).getBytes());

            PBFTCohort.Client thriftConnection = null;
            try {
                thriftConnection = member.getThriftConnection();
                thriftConnection.prePrepare(prePrepareMessage, message, transaction);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                member.returnThriftConnection(thriftConnection);
            }
        }
    }

    @Override
    public void prePrepare(PrePrepareMessage message, ClientMessage clientMessage, TTransaction transaction) throws TException {
        LOG.info("Entering prePrepare");
        if (this.configProvider.getLeader().getReplicaID() != message.getReplicaId()) return;
        if (!this.configProvider.getGroupMember(message.getReplicaId()).verifySignature(message, message.getMessageSignature()))
            return;       // Validate signature
        LOG.info("Validated leader signature");
        if(!this.configProvider.getGroupMember(clientMessage.getReplicaId()).verifySignature(clientMessage, clientMessage.getMessageSignature()))
            return;
        LOG.info("Validated client (sender) signature");

        if (transaction.getViewstamp().getViewId() != this.configProvider.getViewID()) return; // Check we're in view v
        LOG.info("Successfully passed view id validation");

        Transaction<Operation<ChineseCheckersState>> logTransaction
                = Transaction.getTransactionForPBFTTransaction(transaction);

        if(!transaction.getOperation().equals(clientMessage.getOperation())) return;
        LOG.info("Leader is telling the truth about client's intentions");

        try {
            log.addEntry(logTransaction);                                                     // Check sequence number
        } catch (IllegalLogEntryException e) {
            e.printStackTrace();
        }

        LOG.info(log);
        multicastPrepare(CryptoUtil.computeTransactionDigest(logTransaction), transaction.viewstamp);
        prepareIfReady(message.getViewstamp(), new TransactionDigest(message.getTransactionDigest()));
        commitIfReady(message.getViewstamp(), new TransactionDigest(message.getTransactionDigest()));
    }

    private void multicastPrepare(TransactionDigest transactionDigest, Viewstamp viewstamp) throws TException {
        for (final GroupMember<PBFTCohort.Client> member : configProvider.getOtherGroupMembers()) {
            final PrepareMessage prepareMessage = new PrepareMessage();
            prepareMessage.viewstamp = viewstamp;
            prepareMessage.replicaId = thisCohort.getReplicaID();
            prepareMessage.transactionDigest = ByteBuffer.wrap(transactionDigest.getBytes());
            prepareMessage.messageSignature = ByteBuffer.wrap(CryptoUtil.computeMessageSignature(prepareMessage, thisCohort.getPrivateKey()).getBytes());

            PBFTCohort.Client thriftConnection = null;
            try {
                thriftConnection = member.getThriftConnection();
                thriftConnection.prepare(prepareMessage);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                member.returnThriftConnection(thriftConnection);
            }
        }
    }

    private void sendPrepare(final PrepareMessage prepareMessage, final GroupMember<PBFTCohort.Client> target) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                PBFTCohort.Client thriftConnection = null;
                try {
                    thriftConnection = target.getThriftConnection();
                    thriftConnection.prepare(prepareMessage);
                } catch (TException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    target.returnThriftConnection(thriftConnection);
                }
            }
        });
    }

    @Override
    public void prepare(PrepareMessage message) throws TException {
        LOG.info("Entering prepare");
        if (!this.configProvider.getGroupMember(message.getReplicaId()).verifySignature(message, message.getMessageSignature()))
            throw new TException("Failed to validate signature.");       // Validate signature
        LOG.info("Validated signature");
        if (message.getViewstamp().getViewId() != this.configProvider.getViewID())
            throw new TException("Failed to validate view number"); // Check we're in view v
        log.addPrepareMessage(message);
        prepareIfReady(message.getViewstamp(), new TransactionDigest(message.getTransactionDigest()));
    }

    private void prepareIfReady(Viewstamp viewstamp, TransactionDigest transactionDigest) {
        if (!log.readyToPrepare(viewstamp, transactionDigest, configProvider.getQuorumSize())) return;
        log.markAsPrepared(viewstamp);


        for (final GroupMember<PBFTCohort.Client> member : configProvider.getOtherGroupMembers()) {
            final CommitMessage commitMessage = new CommitMessage();
            commitMessage.viewstamp = viewstamp;
            commitMessage.replicaId = thisCohort.getReplicaID();
            commitMessage.transactionDigest = ByteBuffer.wrap(transactionDigest.getBytes());
            commitMessage.messageSignature = ByteBuffer.wrap(CryptoUtil.computeMessageSignature(commitMessage, thisCohort.getPrivateKey()).getBytes());

            pool.execute(new Runnable() {
                @Override
                public void run() {
                    PBFTCohort.Client thriftConnection = null;
                    try {
                        LOG.info("Sending commit message to: " + member.getReplicaID());
                        thriftConnection = member.getThriftConnection();
                        thriftConnection.commit(commitMessage);
                    } catch (TException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        member.returnThriftConnection(thriftConnection);
                    }
                }
            });
        }
    }

    private boolean shouldCheckpoint(int lastCommitted) {
        return (lastCommitted % CHECKPOINT_INTERVAL == 0);
    }

    @Override
    public void commit(CommitMessage message) throws TException {
        LOG.info("Entering commit");

        if (!this.configProvider.getGroupMember(message.getReplicaId()).verifySignature(message, message.getMessageSignature()))
            return;       // Validate signature
        if (message.getViewstamp().getViewId() != this.configProvider.getViewID()) return; // Check we're in view v
        log.addCommitMessage(message);
        commitIfReady(message.getViewstamp(), new TransactionDigest(message.getTransactionDigest()));
    }

    private void commitIfReady(Viewstamp viewstamp, TransactionDigest transactionDigest) throws TException {
        if (!log.readyToCommit(viewstamp, transactionDigest, configProvider.getQuorumSize())) return;
        try {
            log.commitEntry(viewstamp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            int lastCommited = log.getLastApplied();
            if (shouldCheckpoint(lastCommited)) {
                // checkpoint and multicast a proof
                TransactionDigest digest = null; // CryptoUtil.computeCheckpointDigest(); TODO (Susan) need access to state machine
                final CheckpointMessage checkpointMessage = new CheckpointMessage();
                checkpointMessage.setSequenceNumber(lastCommited);
                checkpointMessage.setCheckpointDigest(digest.getBytes());
                checkpointMessage.setReplicaId(replicaID);

                for (final GroupMember<PBFTCohort.Client> target : configProvider.getGroupMembers()) {
                    pool.execute(buildAsyncCheckpointMessage(checkpointMessage, target));
                }
            }
        }
    }

    private Runnable buildAsyncCheckpointMessage(final CheckpointMessage checkpointMessage, final GroupMember<PBFTCohort.Client> target) {
        return new Runnable() {
            @Override
            public void run() {
                PBFTCohort.Client thriftConnection = null;
                try {
                    thriftConnection = target.getThriftConnection();
                    thriftConnection.checkpoint(checkpointMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    target.returnThriftConnection(thriftConnection);
                }
            }
        };
    }

    @Override
    public void checkpoint(CheckpointMessage message) throws TException {
        LOG.info("Entering checkpoint");

    }


    private List<PrePrepareMessage> createPrePrepareForCurrentSeqno(
            int newViewID,
            boolean verify,
            List<ViewChangeMessage> viewChangeMessages /* this is script V in the paper */) {

        // this is computing script O in the pbft paper
        List<PrePrepareMessage> prePrepareMessages = Lists.newArrayList(); // order is important for when we verify
        int max_seqno = MIN_SEQ_NO - 1;
        int lastCheckpointInViewChangeMessages = MIN_SEQ_NO - 1;
        for (ViewChangeMessage viewChangeMessage : viewChangeMessages) {
            for (PrePrepareMessage prePrepareMessage : viewChangeMessage.getPreparedGreaterThanSequenceNumber()) {
                if (max_seqno < prePrepareMessage.getViewstamp().getSequenceNumber())
                    max_seqno = prePrepareMessage.getViewstamp().getSequenceNumber();
            }
            if (lastCheckpointInViewChangeMessages < viewChangeMessage.getSequenceNumber()) {
                lastCheckpointInViewChangeMessages = viewChangeMessage.getSequenceNumber();
            }
        }

        for (int n = LAST_CHECKPOINT; n < lastCheckpointInViewChangeMessages; ++n) {
            int highestViewID = MIN_VIEW_ID - 1;
            byte[] digest = null;
            for (ViewChangeMessage viewChangeMessage : viewChangeMessages) {
                for (PrePrepareMessage prePrepareMessage : viewChangeMessage.getPreparedGreaterThanSequenceNumber()) {
                    if (prePrepareMessage.getViewstamp().getSequenceNumber() == n) {
                        if (highestViewID < prePrepareMessage.getViewstamp().getViewId()) {
                            highestViewID = prePrepareMessage.getViewstamp().getViewId();
                            digest = prePrepareMessage.getTransactionDigest();
                        }
                    }
                }
            }

            PrePrepareMessage prePrepareMessage = new PrePrepareMessage();
            prePrepareMessage.getViewstamp().setViewId(newViewID);
            prePrepareMessage.getViewstamp().setSequenceNumber(n);
            if (highestViewID >= MIN_VIEW_ID) {
                prePrepareMessage.setTransactionDigest(digest);
            } else {
                prePrepareMessage.setTransactionDigest(NO_OP_TRANSACTION_DIGEST);
            }

            if (!verify) {
                prePrepareMessage.setMessageSignature(CryptoUtil.computeMessageSignature(prePrepareMessage, thisCohort.getPrivateKey()).getBytes());
            }
            prePrepareMessages.add(prePrepareMessage);
        }
        return prePrepareMessages;
    }

    private boolean prePrepareSetValid(List<PrePrepareMessage> prePrepareMessages, List<Set<PrepareMessage>> prepareMessages) {
        Map<ByteBuffer, Integer> numPrepares = new HashMap<ByteBuffer, Integer>();
        for (int i = 0; i < prePrepareMessages.size(); ++i) {
            GroupMember<PBFTCohort.Client> sender = configProvider.getGroupMember(prePrepareMessages.get(i).getReplicaId());
            if (!sender.verifySignature(prePrepareMessages.get(i), prePrepareMessages.get(i).getMessageSignature())) {
                return false;
            }
            if (prepareMessages.get(i).size() < configProvider.getQuorumSize()) return false;
            // verify each of the messages
            for (PrepareMessage prepareMessage : prepareMessages.get(i)) {
                if (!prepareMessage.getViewstamp().equals(prePrepareMessages.get(i).getViewstamp())) return false;
                sender = configProvider.getGroupMember(prepareMessage.getReplicaId());
                if (!sender.verifySignature(prepareMessage, prepareMessage.getMessageSignature())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void initiateViewChange() throws TException {
        // some stuff empty for now
        final ViewChangeMessage viewChangeMessage = new ViewChangeMessage();
        viewChangeMessage.setSequenceNumber(MIN_SEQ_NO).setReplicaID(replicaID)
                .setCheckpointProof(new ArrayList<CheckpointMessage>())
                .setNewViewID(configProvider.getViewID() + 1)
                .setPreparedGreaterThanSequenceNumber(new ArrayList<PrePrepareMessage>())
                .setPrepareMessages(new ArrayList<Set<PrepareMessage>>());
        viewChangeMessage.setMessageSignature(CryptoUtil.computeMessageSignature(viewChangeMessage, thisCohort.getPrivateKey()).getBytes());

        for (final GroupMember<PBFTCohort.Client> groupMember : configProvider.getOtherGroupMembers()) {
            pool.execute(new Runnable() {
                public void run() {
                    PBFTCohort.Client thriftConnection = null;
                    try {
                        thriftConnection = groupMember.getThriftConnection();
                        LOG.info("initiating a view change to view " + (configProvider.getViewID() + 1));
                        thriftConnection.startViewChange(viewChangeMessage);
                    } catch (TException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        groupMember.returnThriftConnection(thriftConnection);
                    }
                }
            });
        }

    }

    @Override
    public synchronized void startViewChange(ViewChangeMessage message) throws TException {
        LOG.info("Replica " + replicaID + " received view-change message from " + message.getReplicaID() + " suggesting that we change to view " + message.getNewViewID());
        if (message.isSetNewViewID()) {
            int newViewID = message.getNewViewID();
            if (newViewID > configProvider.getViewID()) { // can only move to a higher view
                // TODO: verify of checkpoint messages
                if (!prePrepareSetValid(message.getPreparedGreaterThanSequenceNumber(), message.getPrepareMessages())) {
                    return;
                }

                if (viewChangeMessages.containsKey(newViewID)) {
                    viewChangeMessages.get(newViewID).add(message);
                } else {
                    viewChangeMessages.put(newViewID, Lists.newArrayList(message));
                }

                // if primary, check if you have enough to send NewViewMessage
                LOG.info("new primary should be " + message.getNewViewID() % (configProvider.getGroupMembers().size()));
                LOG.info("Replica " + replicaID + " has " + viewChangeMessages.get(newViewID).size() + "view change suggestions");
                LOG.info("Replica " + replicaID + " thinks that quorum size is " + configProvider.getQuorumSize());
                if (message.getNewViewID() % (configProvider.getGroupMembers().size()) == replicaID
                        && (viewChangeMessages.get(newViewID).size()+1) >= configProvider.getQuorumSize()) {
                    LOG.info("Replica " + replicaID + " will be the new primary ");
                    configProvider.setViewID(message.getNewViewID());
                    // multicast NEW-VIEW message
                    Set<GroupMember<PBFTCohort.Client>> groupMembers = configProvider.getOtherGroupMembers();
                    for (final GroupMember<PBFTCohort.Client> groupMember : groupMembers) {
                        final NewViewMessage newViewMessage = new NewViewMessage();
                        newViewMessage.setReplicaID(replicaID);
                        newViewMessage.setNewViewID(newViewID);
                        newViewMessage.setViewChangeMessages(viewChangeMessages.get(newViewID));
                        // copy the hashset here because the message could be sent after we leave this method
                        // and start modifying viewChangeMessages again
                        newViewMessage.setPrePrepareMessages(
                                createPrePrepareForCurrentSeqno(newViewID, false, Lists.newArrayList(viewChangeMessages.get(newViewID))));
                        newViewMessage.setMessageSignature(CryptoUtil.computeMessageSignature(newViewMessage, thisCohort.getPrivateKey()).getBytes());
                        pool.execute(new Runnable() {
                            public void run() {
                                PBFTCohort.Client thriftConnection = null;
                                try {
                                    thriftConnection = groupMember.getThriftConnection();
                                    thriftConnection.approveViewChange(newViewMessage);
                                } catch (TException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    groupMember.returnThriftConnection(thriftConnection);
                                }
                            }
                        });

                    }
                }
            }
        }
    }

    @Override
    public synchronized void approveViewChange(NewViewMessage message) throws TException {
        LOG.info("Being told to approve to view change by " + message.getReplicaID() + " with message " + message);
        int senderReplicaID = message.getReplicaID();
        GroupMember<PBFTCohort.Client> sender = configProvider.getGroupMember(senderReplicaID);

        // verify the NewViewMessage
        if (!sender.verifySignature(message, message.getMessageSignature())) {
            return;
        }

        // verify the ViewChangeMessages
        for (ViewChangeMessage viewChangeMessage : message.getViewChangeMessages()) {
            if (viewChangeMessage.getNewViewID() != message.getNewViewID()) {
                return;
            }
            sender = configProvider.getGroupMember(viewChangeMessage.getReplicaID());
            if (!sender.verifySignature(viewChangeMessage, viewChangeMessage.getMessageSignature())) {
                return;
            }
        }

        // verify the preprepares
        List<PrePrepareMessage> recomputedPrePrepareMessages = createPrePrepareForCurrentSeqno(
                message.getNewViewID(), true, message.getViewChangeMessages());
        // should be the same length
        if (recomputedPrePrepareMessages.size() != message.getPrePrepareMessages().size()) {
            return;
        }

        for (int i = 0; i < recomputedPrePrepareMessages.size(); ++i) {
            PrePrepareMessage received = message.getPrePrepareMessages().get(i);
            PrePrepareMessage recomputed = recomputedPrePrepareMessages.get(i);
            sender = configProvider.getGroupMember(recomputed.getReplicaId());
            // check that recomputed contents are the same
            if (!received.getTransactionDigest().equals(recomputed.getTransactionDigest())
                    || !received.getViewstamp().equals(recomputed.getViewstamp())
                    || received.getReplicaId() != recomputed.getReplicaId()) {
                return;
            }

            // verify signatures
            if (!sender.verifySignature(message.getPrePrepareMessages().get(i), message.getPrePrepareMessages().get(i).getMessageSignature())) {
                return;
            }
        }

        // change to new view
        configProvider.setViewID(message.getNewViewID());
        LOG.info("replica " + replicaID + " is in view " + message.getNewViewID());

        // send prepares for everything in script O
        for (PrePrepareMessage prePrepareMessage : message.getPrePrepareMessages()) {
            TransactionDigest transactionDigest = new TransactionDigest(prePrepareMessage.getTransactionDigest());
            Viewstamp viewstamp = new Viewstamp(
                    prePrepareMessage.getViewstamp().getSequenceNumber(), configProvider.getViewID());
            multicastPrepare(transactionDigest, viewstamp);
            if (log.getTransaction(viewstamp) == null) {
                // if we don't have this in our log already, we need to ask someone else for it
                GroupMember<PBFTCohort.Client> target = getReplicaThatPreparedSeqno(message, viewstamp.getSequenceNumber());
                Preconditions.checkNotNull(target);

                PBFTCohort.Client thriftConnection = null;
                try {
                    thriftConnection = target.getThriftConnection();
                    TTransaction thriftTransaction = thriftConnection.getTransaction(new AskForTransaction().setReplicaID(replicaID).setViewstamp(viewstamp));
                    common.Transaction<Operation<ChineseCheckersState>> logTransaction = common.Transaction.getTransactionForPBFTTransaction(
                            thriftTransaction);
                    log.addEntry(logTransaction);
                } catch (IllegalLogEntryException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    target.returnThriftConnection(thriftConnection);
                }
            }
        }

        // clear old entries for old views out from viewChangeMessages
        for (Iterator<Map.Entry<Integer, List<ViewChangeMessage>>> it
                     = viewChangeMessages.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, List<ViewChangeMessage>> entry = it.next();
            if (entry.getKey().compareTo(configProvider.getViewID()) <= 0) {
                it.remove();
            }
        }
    }

    @Nullable
    private GroupMember<PBFTCohort.Client> getReplicaThatPreparedSeqno(NewViewMessage newViewMessage, int sequenceNumber) {
        // look through V
        // look through P,
        // find prepare with that seqno and look through corresponding prepares to find someone who sent one
        for (ViewChangeMessage viewChangeMessage : newViewMessage.getViewChangeMessages()) {
            List<PrePrepareMessage> prePrepareMessages = viewChangeMessage.getPreparedGreaterThanSequenceNumber();
            List<Set<PrepareMessage>> prepareMessages = viewChangeMessage.getPrepareMessages();
            for (int i = 0; i < prePrepareMessages.size(); ++i) {
                if (prePrepareMessages.get(i).getViewstamp().getSequenceNumber() == sequenceNumber) {
                    if (prepareMessages.get(i).iterator().hasNext()) {
                        return configProvider.getGroupMember(prepareMessages.get(i).iterator().next().getReplicaId());
                    }
                }
            }
        }
        return null;
    }

    @Override
    public TTransaction getTransaction(AskForTransaction message) throws TException {
        // return log.getTransaction(message.getViewstamp()).toThriftTransaction();
        return null;
    }

    @Override
    public void ping() throws TException {
        LOG.info("Ping!!!!");
    }
}
