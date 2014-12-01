package server;

import PBFT.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import common.CryptoUtil;
import common.IllegalLogEntryException;
import common.Log;
import common.TransactionDigest;
import config.GroupConfigProvider;
import config.GroupMember;
import gameengine.ChineseCheckersOperationFactory;
import gameengine.ChineseCheckersState;
import gameengine.operations.NoOp;
import org.apache.thrift.TException;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by andrew on 11/27/14.
 */
public class PBFTCohortHandler implements PBFTCohort.Iface {
    private final Log<statemachine.Operation<ChineseCheckersState>> log;
    private GroupConfigProvider<PBFTCohort.Client> configProvider;
    private final GroupMember<PBFTCohort.Client> thisCohort;
    private Map<Integer, List<ViewChangeMessage>> viewChangeMessages; // this should include your own messages
    private int replicaID;
    private static final int POOL_SIZE = 10;
    private final ExecutorService pool;
    private static final int LAST_CHECKPOINT = 0; // set to 0 for now; no checkpointing
    private static final int MIN_SEQ_NO = 0;
    private static final int MIN_VIEW_ID = 0;
    private static final byte[] NO_OP_TRANSACTION_DIGEST = CryptoUtil.computeTransactionDigest(
            new common.Transaction(null, -1, new NoOp())).getBytes();


    public PBFTCohortHandler(GroupConfigProvider<PBFTCohort.Client> configProvider, int replicaID, GroupMember<PBFTCohort.Client> thisCohort) {
        this.configProvider = configProvider;
        viewChangeMessages = Maps.newHashMap();
        this.replicaID = replicaID;
        pool = Executors.newFixedThreadPool(POOL_SIZE);
        this.log = new Log<statemachine.Operation<ChineseCheckersState>>();
        this.thisCohort = thisCohort;
    }

    @Override
    public void prePrepare(PrePrepareMessage message, Transaction transaction) throws TException {
        if(!this.configProvider.getGroupMember(message.getReplicaId()).verifySignature(message, message.getMessageSignature())) return;       // Validate signature
        if(transaction.getViewstamp().getViewId() != this.configProvider.getViewID()) return; // Check we're in view v

        common.Transaction<statemachine.Operation<ChineseCheckersState>> logTransaction = getTransactionForPBFTTransaction(transaction);

        try {
            log.addEntry(logTransaction);                                                     // Check sequence number
        } catch (IllegalLogEntryException e) {
            e.printStackTrace();
        }

        multicastPrepare(CryptoUtil.computeTransactionDigest(logTransaction), transaction.viewstamp);
    }

    private void multicastPrepare(TransactionDigest transactionDigest, Viewstamp viewstamp) {
        for (final GroupMember<PBFTCohort.Client> member : configProvider.getGroupMembers()) {
            final PrepareMessage prepareMessage = new PrepareMessage();
            prepareMessage.viewstamp = viewstamp;
            prepareMessage.replicaId = thisCohort.getReplicaID();
            prepareMessage.transactionDigest = ByteBuffer.wrap(transactionDigest.getBytes());
            prepareMessage.messageSignature = ByteBuffer.wrap(CryptoUtil.computeMessageSignature(prepareMessage, thisCohort.getPrivateKey()).getBytes());
        }

    }

    private void sendPrepare(final PrepareMessage prepareMessage, final GroupMember<PBFTCohort.Client> target) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    target.getThriftConnection().prepare(prepareMessage);
                } catch (TException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void prepare(PrepareMessage message) throws TException {
        if(!this.configProvider.getGroupMember(message.getReplicaId()).verifySignature(message, message.getMessageSignature())) return;       // Validate signature
        if(message.getViewstamp().getViewId() != this.configProvider.getViewID()) return; // Check we're in view v
        log.addPrepareMessage(message);
        if(!log.readyToPrepare(message, configProvider.getQuorumSize())) return;
        log.markAsPrepared(message.getViewstamp());

        for (final GroupMember<PBFTCohort.Client> member : configProvider.getGroupMembers()) {
            final CommitMessage commitMessage = new CommitMessage();
            commitMessage.viewstamp = message.getViewstamp();
            commitMessage.replicaId = thisCohort.getReplicaID();
            commitMessage.transactionDigest = ByteBuffer.wrap(message.getTransactionDigest());
            commitMessage.messageSignature = ByteBuffer.wrap(CryptoUtil.computeMessageSignature(commitMessage, thisCohort.getPrivateKey()).getBytes());

            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        member.getThriftConnection().commit(commitMessage);
                    } catch (TException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    @Override
    public void commit(CommitMessage message) throws TException {
        if(!this.configProvider.getGroupMember(message.getReplicaId()).verifySignature(message, message.getMessageSignature())) return;       // Validate signature
        if(message.getViewstamp().getViewId() != this.configProvider.getViewID()) return; // Check we're in view v
        log.addCommitMessage(message);
        if(!log.readyToCommit(message, configProvider.getQuorumSize())) return;
        log.commitEntry(message.getViewstamp());
    }

    @Override
    public void checkpoint(CheckpointMessage message) throws TException {

    }

    private List<PrePrepareMessage> createPrePrepareForCurrentSeqno(
            int newViewID,
            boolean verify,
            List<ViewChangeMessage> viewChangeMessages /* this is script V in the paper */ ) {

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
        for(int i=0; i < prePrepareMessages.size(); ++i) {
            GroupMember<PBFTCohort.Client> sender = configProvider.getGroupMember(prePrepareMessages.get(i).getReplicaId());
            if (!sender.verifySignature(prePrepareMessages.get(i),prePrepareMessages.get(i).getMessageSignature())) {
                return false;
            }
            if (prepareMessages.get(i).size() < configProvider.getQuorumSize()) return false;
            // verify each of the messages
            for (PrepareMessage prepareMessage: prepareMessages.get(i)) {
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
    public synchronized void startViewChange(ViewChangeMessage message) throws TException {
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
                if (configProvider.getLeader().getReplicaID() == replicaID
                        && viewChangeMessages.get(newViewID).size() > configProvider.getQuorumSize()) {
                    // multicast NEW-VIEW message
                    Set<GroupMember<PBFTCohort.Client>> groupMembers = configProvider.getGroupMembers();
                    for (final GroupMember<PBFTCohort.Client> groupMember : groupMembers) {
                        final NewViewMessage newViewMessage = new NewViewMessage();
                        newViewMessage.setNewViewID(newViewID);
                        newViewMessage.setViewChangeMessages(viewChangeMessages.get(newViewID));
                        // copy the hashset here because the message could be sent after we leave this method
                        // and start modifying viewChangeMessages again
                        newViewMessage.setPrePrepareMessages(
                                createPrePrepareForCurrentSeqno(newViewID, false, Lists.newArrayList(viewChangeMessages.get(newViewID))));
                        pool.execute(new Runnable() {
                            public void run() {
                                try {
                                    groupMember.getThriftConnection().approveViewChange(newViewMessage);
                                } catch (TException e) {
                                    e.printStackTrace();
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

        for (int i=0; i<recomputedPrePrepareMessages.size(); ++i) {
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

        // send prepares for everything in script O
        for (PrePrepareMessage prePrepareMessage : message.getPrePrepareMessages()) {
            TransactionDigest transactionDigest = new TransactionDigest(prePrepareMessage.getTransactionDigest());
            Viewstamp viewstamp = new Viewstamp(
                    prePrepareMessage.getViewstamp().getSequenceNumber(), configProvider.getViewID());
            multicastPrepare(transactionDigest, viewstamp);
            if (log.getTransaction(viewstamp) == null ) {
                // if we don't have this in our log already, we need to ask someone else for it
                GroupMember<PBFTCohort.Client> target = getReplicaThatPreparedSeqno(viewstamp.getSequenceNumber());
                common.Transaction<statemachine.Operation<ChineseCheckersState>> logTransaction = getTransactionForPBFTTransaction(
                        target.getThriftConnection().getTransaction(new AskForTransaction().setReplicaID(replicaID).setViewstamp(viewstamp)));
                try {
                    log.addEntry(logTransaction);
                } catch (IllegalLogEntryException e) {
                    e.printStackTrace();
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

    private static GroupMember<PBFTCohort.Client> getReplicaThatPreparedSeqno(int sequenceNumber) {
        // look through V
        // look through P,
        // find preprepare with that seqno and look through corresponding prepares to find someone who sent one
        return null;
    }

    @Override
    public Transaction getTransaction(AskForTransaction message) throws TException {
        return null;
    }

    private static common.Transaction<statemachine.Operation<ChineseCheckersState>> getTransactionForPBFTTransaction(Transaction transaction) {
        return new common.Transaction<statemachine.Operation<ChineseCheckersState>>(
                transaction.viewstamp,
                transaction.viewstamp.getSequenceNumber(),
                ChineseCheckersOperationFactory.hydrate(transaction.operation)
        );
    }
}
