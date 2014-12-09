package server;

import PBFT.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.istack.internal.Nullable;
import common.*;
import config.GroupConfigProvider;
import config.GroupMember;
import gameengine.ChineseCheckersState;
import gameengine.GameEngine;
import gameengine.operations.NoOp;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import statemachine.Operation;
import statemachine.StateMachine;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static PBFT.PBFTCohort.Iface;

/**
 * Created by andrew on 11/27/14.
 */
public class PBFTCohortHandler implements Iface, StateMachineCheckpointListener {
    private static Logger LOG = LogManager.getLogger(PBFTCohortHandler.class);
    private final Log<Operation<ChineseCheckersState>> log;
    private GroupConfigProvider<PBFTCohort.Client> configProvider;
    private final GroupMember<PBFTCohort.Client> thisCohort;

    private int replicaID;
    private final ExecutorService pool;

    private static final int POOL_SIZE = 10;
    private static final int MIN_SEQ_NO = 0;
    private static final int MIN_VIEW_ID = 0;
    private static final byte[] NO_OP_TRANSACTION_DIGEST = CryptoUtil.computeDigest(
            new common.Transaction(null, -1, new NoOp(), 0)).getBytes();

    private int sequenceNumber = -1;
    private StateMachine stateMachine; // we want to ask it for its most recent checkpoint for view change

    public PBFTCohortHandler(GroupConfigProvider<PBFTCohort.Client> configProvider, int replicaID, GroupMember<PBFTCohort.Client> thisCohort, GameEngine toNotify) {
        Thread.currentThread().setName("{SERVER ID: " + replicaID + "} " + Thread.currentThread().getName());
        this.configProvider = configProvider;
        this.replicaID = replicaID;
        pool = Executors.newFixedThreadPool(POOL_SIZE);
        this.log = new Log<>();
        this.thisCohort = thisCohort;
        this.log.addListener(toNotify);
        this.stateMachine = toNotify.getStateMachine();
        this.stateMachine.addCheckpointListener(this);

        LOG.info("Starting handler!");
    }

    @Override
    synchronized public void clientMessage(final ClientMessage message) throws TException {
        LOG.info("Got client message");
        if(this.configProvider.getLeader().getReplicaID() != this.replicaID) return;
        LOG.info("I'm the leader");
        if (!this.configProvider.getGroupMember(message.getReplicaId()).verifySignature(message, message.getMessageSignature())) return;
        LOG.info("validated signature! multicasting prePrepares...");

        final TTransaction transaction = new TTransaction();
        transaction.viewstamp = new Viewstamp(sequenceNumber + 1, configProvider.getViewID());
        transaction.replicaId = message.getReplicaId();
        transaction.operation = message.operation;

        LOG.info("Attempting to transmit with sequence number: " + (sequenceNumber + 1));

        sequenceNumber++;

        for (final GroupMember<PBFTCohort.Client> member : configProvider.getGroupMembers()) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    final PrePrepareMessage prePrepareMessage = new PrePrepareMessage();
                    prePrepareMessage.viewstamp = transaction.getViewstamp();
                    prePrepareMessage.replicaId = thisCohort.getReplicaID();
                    prePrepareMessage.transactionDigest = ByteBuffer.wrap(
                            CryptoUtil.computeDigest(Transaction.getTransactionForPBFTTransaction(transaction)).getBytes()
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
            });
        }
    }

    @Override
    public void prePrepare(PrePrepareMessage message, ClientMessage clientMessage, TTransaction transaction) throws TException {
        LOG.trace("Entering prePrepare");
        if (this.configProvider.getLeader().getReplicaID() != message.getReplicaId()) throw new TException("Replica ID check failed");
        if (!this.configProvider.getGroupMember(message.getReplicaId()).verifySignature(message, message.getMessageSignature())) throw new TException("Preprepare message signature validation failed");       // Validate signature
        LOG.trace("Validated leader signature");
        if(!this.configProvider.getGroupMember(clientMessage.getReplicaId()).verifySignature(clientMessage, clientMessage.getMessageSignature())) throw new TException("Client message signature validation failed");
        LOG.trace("Validated client (sender) signature");

        if (transaction.getViewstamp().getViewId() != this.configProvider.getViewID()) throw new TException("View id validation failed"); // Check we're in view v
        LOG.trace("Successfully passed view id validation");

        Transaction<Operation<ChineseCheckersState>> logTransaction
                = Transaction.getTransactionForPBFTTransaction(transaction);

        if(!transaction.getOperation().equals(clientMessage.getOperation())) throw new TException("Leader is not telling the truth about the client's intentions");
        LOG.trace("Leader is telling the truth about client's intentions");

        try {
            log.addEntry(logTransaction, message);                                                     // Check sequence number
        } catch (IllegalLogEntryException e) {
            e.printStackTrace();
        }

        multicastPrepare(CryptoUtil.computeDigest(logTransaction), transaction.viewstamp);
        prepareIfReady(message.getViewstamp(), new Digest(message.getTransactionDigest()));
        commitIfReady(message.getViewstamp(), new Digest(message.getTransactionDigest()));
    }

    private void multicastPrepare(Digest transactionDigest, Viewstamp viewstamp) throws TException {
        final PrepareMessage prepareMessage = new PrepareMessage();
        prepareMessage.viewstamp = viewstamp;
        prepareMessage.replicaId = thisCohort.getReplicaID();
        prepareMessage.transactionDigest = ByteBuffer.wrap(transactionDigest.getBytes());
        prepareMessage.messageSignature = ByteBuffer.wrap(CryptoUtil.computeMessageSignature(prepareMessage, thisCohort.getPrivateKey()).getBytes());
        for (final GroupMember<PBFTCohort.Client> member : configProvider.getGroupMembers()) {

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

    @Override
    public void prepare(PrepareMessage message) throws TException {
        LOG.trace("Entering prepare");
        if (!this.configProvider.getGroupMember(message.getReplicaId()).verifySignature(message, message.getMessageSignature()))
            throw new TException("Failed to validate signature.");       // Validate signature
        LOG.trace("Validated signature");
        if (message.getViewstamp().getViewId() != this.configProvider.getViewID())
            throw new TException("Failed to validate view number"); // Check we're in view v
        log.addPrepareMessage(message);
        prepareIfReady(message.getViewstamp(), new Digest(message.getTransactionDigest()));
    }

    private void prepareIfReady(Viewstamp viewstamp, Digest transactionDigest) {
        if (!log.readyToPrepare(viewstamp, transactionDigest, configProvider.getQuorumSize())) return;
        log.markAsPrepared(viewstamp);

        final CommitMessage commitMessage = new CommitMessage();
        commitMessage.viewstamp = viewstamp;
        commitMessage.replicaId = thisCohort.getReplicaID();
        commitMessage.transactionDigest = ByteBuffer.wrap(transactionDigest.getBytes());
        commitMessage.messageSignature = ByteBuffer.wrap(CryptoUtil.computeMessageSignature(commitMessage, thisCohort.getPrivateKey()).getBytes());
        for (final GroupMember<PBFTCohort.Client> member : configProvider.getGroupMembers()) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    PBFTCohort.Client thriftConnection = null;
                    try {
                        LOG.trace("Sending commit message to: " + member.getReplicaID());
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

    @Override
    public void commit(CommitMessage message) throws TException {
        LOG.trace("Entering commit");
        if (!this.configProvider.getGroupMember(message.getReplicaId()).verifySignature(message, message.getMessageSignature())) return;       // Validate signature
        if (message.getViewstamp().getViewId() != this.configProvider.getViewID()) return; // Check we're in view v
        log.addCommitMessage(message);
        commitIfReady(message.getViewstamp(), new Digest(message.getTransactionDigest()));
    }

    private void commitIfReady(Viewstamp viewstamp, Digest transactionDigest) throws TException {
        if (!log.readyToCommit(viewstamp, transactionDigest, configProvider.getQuorumSize())) return;
        try {
            log.commitEntry(viewstamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    synchronized public void checkpoint(CheckpointMessage message) throws TException {
        LOG.trace("Got a checkpoint message");
        if (!thisCohort.verifySignature(message, message.getMessageSignature())) return;
        LOG.trace("Verified checkpoint message");
        // don't bother adding old checkpoint messages
        if (log.getLastStableCheckpoint() >= message.getSequenceNumber()) return;
        log.addCheckpointMessage(message, configProvider.getQuorumSize());
    }

    private List<PrePrepareMessage> createPrePreparesToFillPreparedSeqnoHoles(
            int newViewID,
            boolean verify,
            Set<ViewChangeMessage> viewChangeMessages /* this is script V in the paper */) {

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

        for (int n = lastCheckpointInViewChangeMessages; n < max_seqno; ++n) {
            prePrepareMessages.add(
                    createPrePrepareToFillHole(n, viewChangeMessages, newViewID, verify));
        }
        return prePrepareMessages;
    }

    private PrePrepareMessage createPrePrepareToFillHole(
            int seqno,
            Set<ViewChangeMessage> viewChangeMessages,
            int newViewID,
            boolean verify) {
        int highestViewID = MIN_VIEW_ID - 1;
        byte[] digest = null;
        for (ViewChangeMessage viewChangeMessage : viewChangeMessages) {
            for (PrePrepareMessage prePrepareMessage : viewChangeMessage.getPreparedGreaterThanSequenceNumber()) {
                if (prePrepareMessage.getViewstamp().getSequenceNumber() == seqno) {
                    if (highestViewID < prePrepareMessage.getViewstamp().getViewId()) {
                        highestViewID = prePrepareMessage.getViewstamp().getViewId();
                        digest = prePrepareMessage.getTransactionDigest();
                    }
                }
            }
        }

        PrePrepareMessage prePrepareMessage = new PrePrepareMessage();
        prePrepareMessage.getViewstamp().setViewId(newViewID);
        prePrepareMessage.getViewstamp().setSequenceNumber(seqno);
        if (highestViewID >= MIN_VIEW_ID) {
            prePrepareMessage.setTransactionDigest(digest);
        } else {
            prePrepareMessage.setTransactionDigest(NO_OP_TRANSACTION_DIGEST);
        }

        if (!verify) {
            prePrepareMessage.setMessageSignature(CryptoUtil.computeMessageSignature(prePrepareMessage, thisCohort.getPrivateKey()).getBytes());
        }
        return prePrepareMessage;
    }

    private boolean prePrepareSetValid(List<PrePrepareMessage> prePrepareMessages, List<Set<PrepareMessage>> prepareMessages) {
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
    synchronized public void initiateViewChange() throws TException {
        //  TODO (Susan): once you do this, until you finish the view change you should ignore all other messages
        // note that checkpoint proof may be for a later checkpoint than prePrepares and proof, but I think that's ok

        Map<PrePrepareMessage, Set<PrepareMessage>> prePreparesAndProof = Maps.newHashMap();
        Set<CheckpointMessage> checkPointProof = Sets.newHashSet();
        int lastStableCheckpoint = log.getPreparesCheckpointProofAndLastStableCheckpoint(prePreparesAndProof, checkPointProof);

        final ViewChangeMessage viewChangeMessage = new ViewChangeMessage();
        viewChangeMessage.setSequenceNumber(lastStableCheckpoint).setReplicaID(replicaID)
                .setCheckpointProof(checkPointProof)
                .setNewViewID(configProvider.getViewID() + 1)
                .setPreparedGreaterThanSequenceNumber(new ArrayList<>(prePreparesAndProof.keySet())) // TODO (Susan): fix this
                .setPrepareMessages(new ArrayList<>(prePreparesAndProof.values())); // TODO (Susan) same as above
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

    private boolean verifyCheckPointMessages(int seqno, Set<CheckpointMessage> checkpointMessages) {
        for (CheckpointMessage checkpointMessage : checkpointMessages) {
            GroupMember<PBFTCohort.Client> sender = configProvider.getGroupMember(checkpointMessage.getReplicaId());
            if (checkpointMessage.getSequenceNumber() != seqno ||
                    !sender.verifySignature(checkpointMessage, checkpointMessage.getMessageSignature())) {
                return false;
            }
        }
        LOG.trace("Checkpoint messages are valid.");
        return true;
    }

    @Override
    public synchronized void startViewChange(ViewChangeMessage message) throws TException {
        LOG.info("Replica " + replicaID + " received view-change message from " + message.getReplicaID() + " suggesting that we change to view " + message.getNewViewID());
        if (message.isSetNewViewID()) {
            int newViewID = message.getNewViewID();
            if (newViewID > configProvider.getViewID()) { // can only move to a higher view

                if (!verifyCheckPointMessages(message.getSequenceNumber(), message.getCheckpointProof())
                        || !prePrepareSetValid(message.getPreparedGreaterThanSequenceNumber(), message.getPrepareMessages())) {
                    return;
                }

                log.addViewChangeMessage(message);

                // if primary, check if you have enough to send NewViewMessage
                LOG.info("new primary should be " + message.getNewViewID() % (configProvider.getGroupMembers().size()));
                if (message.getNewViewID() % (configProvider.getGroupMembers().size()) == replicaID
                        && log.readyToSendNewView(message.getNewViewID(),configProvider.getQuorumSize())) {
                    LOG.info("Replica " + replicaID + " will be the new primary ");
                    // multicast NEW-VIEW message
                    Set<ViewChangeMessage> scriptV = log.getViewChangeMessages(newViewID);
                    final NewViewMessage newViewMessage = new NewViewMessage();
                    newViewMessage.setReplicaID(replicaID);
                    newViewMessage.setNewViewID(newViewID);
                    newViewMessage.setViewChangeMessages(scriptV);
                    newViewMessage.setPrePrepareMessages(
                            createPrePreparesToFillPreparedSeqnoHoles(newViewID, false, scriptV));
                    newViewMessage.setMessageSignature(
                            CryptoUtil.computeMessageSignature(newViewMessage, thisCohort.getPrivateKey()).getBytes());
                    multicastNewView(newViewMessage);
                }
            }
        }
    }

    private void multicastNewView(final NewViewMessage message) {
        for (final GroupMember<PBFTCohort.Client> groupMember : configProvider.getGroupMembers()) {
            pool.execute(new Runnable() {
                public void run() {
                    PBFTCohort.Client thriftConnection = null;
                    try {
                        thriftConnection = groupMember.getThriftConnection();
                        thriftConnection.approveViewChange(message);
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

    private boolean verifyHoleFillingPreprepares(NewViewMessage message) {
        GroupMember<PBFTCohort.Client> sender;
        // verify the preprepares
        List<PrePrepareMessage> recomputedPrePrepareMessages = createPrePreparesToFillPreparedSeqnoHoles(
                message.getNewViewID(), true, message.getViewChangeMessages());
        // should be the same length
        if (recomputedPrePrepareMessages.size() != message.getPrePrepareMessages().size()) {
            return false;
        }

        for (int i = 0; i < recomputedPrePrepareMessages.size(); ++i) {
            PrePrepareMessage received = message.getPrePrepareMessages().get(i);
            PrePrepareMessage recomputed = recomputedPrePrepareMessages.get(i);
            sender = configProvider.getGroupMember(recomputed.getReplicaId());

            // check that recomputed contents are the same
            if (!received.getTransactionDigest().equals(recomputed.getTransactionDigest())
                    || !received.getViewstamp().equals(recomputed.getViewstamp())
                    || received.getReplicaId() != recomputed.getReplicaId()) {
                return false;
            }

            // verify signatures
            if (!sender.verifySignature(message.getPrePrepareMessages().get(i), message.getPrePrepareMessages().get(i).getMessageSignature())) {
                return false;
            }
        }

        LOG.trace("Hole filling preprepares verified");
        return true;
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
            // actually for the right view
            if (viewChangeMessage.getNewViewID() != message.getNewViewID()) {
                return;
            }
            sender = configProvider.getGroupMember(viewChangeMessage.getReplicaID());
            // signatures are right
            if (!sender.verifySignature(viewChangeMessage, viewChangeMessage.getMessageSignature())) {
                return;
            }
        }

        if (!verifyHoleFillingPreprepares(message)) return;

        // change to new view
        configProvider.setViewID(message.getNewViewID());
        LOG.info("replica " + replicaID + " is in view " + message.getNewViewID());

        // send prepares for everything in script O
        for (PrePrepareMessage prePrepareMessage : message.getPrePrepareMessages()) {

            Digest transactionDigest = new Digest(prePrepareMessage.getTransactionDigest());
            Viewstamp viewstamp = prePrepareMessage.getViewstamp();
            multicastPrepare(transactionDigest, viewstamp);
            pool.execute(asyncEnsureTransactionInLog(prePrepareMessage));
        }

        log.markViewChangeCompleted(configProvider.getViewID());
    }

    private Runnable asyncEnsureTransactionInLog(final PrePrepareMessage prePrepareMessage) {
        return new Runnable() {
            @Override
            public void run() {
                common.Transaction<Operation<ChineseCheckersState>> logTransaction
                        = log.getTransaction(prePrepareMessage.getViewstamp());
                if (logTransaction == null) {
                    // if we don't have this in our log already, we need to ask someone else for it
                    // TODO (Susan) : may have to ask many people
                    GroupMember<PBFTCohort.Client> target = configProvider.getGroupMember(prePrepareMessage.getReplicaId());
                    Preconditions.checkNotNull(target);
                    PBFTCohort.Client thriftConnection = null;
                    try {
                        thriftConnection = target.getThriftConnection();
                        TTransaction thriftTransaction = thriftConnection.getTransaction(
                                new AskForTransaction()
                                        .setReplicaID(replicaID)
                                        .setViewstamp(prePrepareMessage.getViewstamp()));
                        logTransaction = common.Transaction.getTransactionForPBFTTransaction(
                                thriftTransaction);
                        log.addEntry(logTransaction, prePrepareMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        target.returnThriftConnection(thriftConnection);
                    }
                }

                try {
                    log.addEntry(logTransaction, prePrepareMessage);
                } catch (IllegalLogEntryException e) {
                    e.printStackTrace();
                }
            }
        };
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
        return common.Transaction.serialize(log.getTransaction(message.getViewstamp()));
    }

    @Override
    public void ping() throws TException {
        LOG.info("Ping!!!!");
    }

    @Override
    public void notifyOnCheckpointed(int seqNo, Digest digest) {
        // TODO (Susan): can I reuse this one object? for all messages
        final CheckpointMessage checkpointMessage = new CheckpointMessage()
                .setCheckpointDigest(digest.getBytes())
                .setReplicaId(thisCohort.getReplicaID())
                .setSequenceNumber(seqNo);
        checkpointMessage.setMessageSignature(
                CryptoUtil.computeMessageSignature(checkpointMessage, thisCohort.getPrivateKey()).getBytes());
        // multicast checkpoint message
        for (final GroupMember<PBFTCohort.Client> member : configProvider.getGroupMembers()) {
            pool.execute(new Runnable() {
                PBFTCohort.Client thriftConnection = null;

                @Override
                public void run() {
                    try {
                        thriftConnection = member.getThriftConnection();
                        thriftConnection.checkpoint(checkpointMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        member.returnThriftConnection(thriftConnection);
                    }
                }
            });
        }

    }
}
