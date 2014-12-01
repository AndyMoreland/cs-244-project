package server;

import PBFT.*;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import common.CryptoUtil;
import common.IllegalLogEntryException;
import common.Log;
import config.GroupConfigProvider;
import config.GroupMember;
import gameengine.ChineseCheckersOperationFactory;
import gameengine.ChineseCheckersState;
import gameengine.operations.NoOp;
import org.apache.thrift.TException;
import statemachine.Operation;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by andrew on 11/27/14.
 */
public class PBFTCohortHandler implements PBFTCohort.Iface {
    private final Log<Operation<ChineseCheckersState>> log;
    private GroupConfigProvider<PBFTCohort.Client> configProvider;
    private final GroupMember<PBFTCohort.Client> thisCohort;
    private Map<Integer, Set<ViewChangeMessage>> viewChangeMessages; // this should include your own messages
    private int replicaID;
    private static final int POOL_SIZE = 10;
    private final ExecutorService pool;
    private static final int LAST_CHECKPOINT = 0; // set to 0 for now; no checkpointing
    private static final int MIN_SEQ_NO = 0;
    private static final int MIN_VIEW_ID = 0;
    private static final byte[] NO_OP_TRANSACTION_DIGEST = CryptoUtil.computeTransactionDigest(
            new common.Transaction(null,-1,new NoOp()));


    public PBFTCohortHandler(GroupConfigProvider<PBFTCohort.Client> configProvider, int replicaID, GroupMember<PBFTCohort.Client> thisCohort) {
        this.configProvider = configProvider;
        viewChangeMessages = Maps.newHashMap();
        this.replicaID = replicaID;
        pool = Executors.newFixedThreadPool(POOL_SIZE);
        this.log = new Log<Operation<ChineseCheckersState>>();
        this.thisCohort = thisCohort;
    }

    @Override
    public void prePrepare(PrePrepareMessage message, Transaction transaction) throws TException {
        assert (configProvider.getLeader().getReplicaID() == this.replicaID);

        common.Transaction<Operation<ChineseCheckersState>> logTransaction = new common.Transaction<Operation<ChineseCheckersState>>(
                transaction.viewstamp,
                transaction.viewstamp.getSequenceNumber(),
                ChineseCheckersOperationFactory.hydrate(transaction.operation)
        );

        try {
            log.addEntry(logTransaction);
        } catch (IllegalLogEntryException e) {
            e.printStackTrace();
        }

        for (final GroupMember<PBFTCohort.Client> member : configProvider.getGroupMembers()) {
            final PrepareMessage prepareMessage = new PrepareMessage();
            prepareMessage.viewstamp = transaction.viewstamp;
            prepareMessage.replicaId = member.getReplicaID();
            prepareMessage.transactionDigest = ByteBuffer.wrap(CryptoUtil.computeTransactionDigest(logTransaction));
            prepareMessage.messageSignature = ByteBuffer.wrap(CryptoUtil.computeMessageSignature(prepareMessage, thisCohort.getPrivateKey()));

            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        member.getThriftConnection().prepare(prepareMessage);
                    } catch (TException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void prepare(PrepareMessage message) throws TException {

    }

    @Override
    public void commit(CommitMessage message) throws TException {

    }

    @Override
    public void checkpoint(CheckpointMessage message) throws TException {

    }

    private  Set<PrePrepareMessage> createPrePrepareForCurrentSeqno(
            int newViewID,
            boolean verify,
            Set<ViewChangeMessage> viewChangeMessages /* this is script V in the paper */ ) {

        // this is computing script O in the pbft paper
        Set<PrePrepareMessage> prePrepareMessages = Sets.newHashSet();
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
                prePrepareMessage.setMessageSignature(CryptoUtil.computeMessageSignature(prePrepareMessage, thisCohort.getPrivateKey()));
            }
            prePrepareMessages.add(prePrepareMessage);
        }
        return prePrepareMessages;
    }


    private boolean prePrepareSetValid(Set<PrePrepareMessage> prePrepareMessages) {
        Map<ByteBuffer, Integer> numPrepares = new HashMap<ByteBuffer, Integer>();
        for (PrePrepareMessage prePrepareMessage: prePrepareMessages) {
            ByteBuffer buf = ByteBuffer.wrap(prePrepareMessage.getTransactionDigest());
            if (numPrepares.containsKey(buf)) {
                numPrepares.put(buf, numPrepares.get(buf) + 1);
            } else {
                numPrepares.put(buf, 1);
            }
        }

        for (ByteBuffer buf :numPrepares.keySet()) {
            if (numPrepares.get(buf) < configProvider.getQuorumSize())
                return false;
        }
        return true;
    }

    @Override
    public synchronized void startViewChange(ViewChangeMessage message) throws TException {
        if (message.isSetNewViewID()) {
            int newViewID = message.getNewViewID();
            if (newViewID > configProvider.getViewID()) { // can only move to a higher view
                // TODO: verify of checkpoint messages
                if (!prePrepareSetValid(message.getPreparedGreaterThanSequenceNumber())) {
                    return;
                }

                if (viewChangeMessages.containsKey(newViewID)) {
                    viewChangeMessages.get(newViewID).add(message);
                } else {
                    viewChangeMessages.put(newViewID, Sets.newHashSet(message));
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
                                createPrePrepareForCurrentSeqno(newViewID, false, Sets.newHashSet(viewChangeMessages.get(newViewID))));
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

        // verify the preprepares FIXME
        /* Set<PrePrepareMessage> prePrepareMessages = createPrePrepareForCurrentSeqno(
                message.getNewViewID(), true, message.getViewChangeMessages());
        for (PrePrepareMessage prePrepareMessage : prePrepareMessages) {
            sender = configProvider.getGroupMember(prePrepareMessage.getReplicaId());
            if (!sender.verifySignature(prePrepareMessage, prePrepareMessage.getMessageSignature())) {
                return;
            }
        } */

        // change to new view
        configProvider.setViewID(message.getNewViewID());

        // send prepares for everything in script O

        // clear old entries for old views out from viewChangeMessages
        for (Iterator<Map.Entry<Integer, Set<ViewChangeMessage>>> it
                     = viewChangeMessages.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Set<ViewChangeMessage>> entry = it.next();
            if (entry.getKey().compareTo(configProvider.getViewID()) <= 0) {
                it.remove();
            }
        }
    }
}
