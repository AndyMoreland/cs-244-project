package server;

import PBFT.*;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import config.GroupConfigProvider;
import config.GroupMember;
import org.apache.thrift.TException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by andrew on 11/27/14.
 */
public class PBFTCohortHandler implements PBFTCohort.Iface {
    private GroupConfigProvider configProvider;
    private Map<Integer,Set<ViewChangeMessage>> viewChangeMessages; // this should include your own messages
    private int replicaID;
    private static final int POOL_SIZE = 10;
    private final ExecutorService pool;
    private static final int LAST_CHECKPOINT = 0; // set to 0 for now; no checkpointing
    private static final int MIN_SEQ_NO = 0;
    private static final int MIN_VIEW_ID = 0;

    public PBFTCohortHandler(GroupConfigProvider<PBFTCohort.Client> configProvider, int replicaID) {
        this.configProvider = configProvider;
        viewChangeMessages = Maps.newHashMap();
        this.replicaID = replicaID;
        pool = Executors.newFixedThreadPool(POOL_SIZE);
    }

    @Override
    public void prePrepare(PrePrepareMessage message, Transaction transaction) throws TException {

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

    private Set<PrePrepareMessage> createPrePrepareForCurrentSeqno(int newViewID, Set<ViewChangeMessage> viewChangeMessages) {
        // this is computing script O in the pbft paper
        Set<PrePrepareMessage> prePrepareMessages = Sets.newHashSet();
        int max_seqno = MIN_SEQ_NO-1;
        int lastCheckpointInViewChangeMessages = MIN_SEQ_NO-1;
        for (ViewChangeMessage viewChangeMessage : viewChangeMessages) {
            for (PrePrepareMessage prePrepareMessage : viewChangeMessage.getPreparedGreaterThanSequenceNumber()) {
                if (max_seqno < prePrepareMessage.getSequenceNumber())
                    max_seqno = prePrepareMessage.getSequenceNumber();
            }
            if (lastCheckpointInViewChangeMessages < viewChangeMessage.getSequenceNumber()) {
                lastCheckpointInViewChangeMessages = viewChangeMessage.getSequenceNumber();
            }
        }

        for (int n=LAST_CHECKPOINT; n<lastCheckpointInViewChangeMessages; ++n) {
            int highestViewID = MIN_VIEW_ID - 1;
            for (ViewChangeMessage viewChangeMessage : viewChangeMessages) {
                for (PrePrepareMessage prePrepareMessage : viewChangeMessage.getPreparedGreaterThanSequenceNumber()) {
                    if (prePrepareMessage.getSequenceNumber() == n) {
                        if (highestViewID < prePrepareMessage.getViewId()) {
                            highestViewID = prePrepareMessage.getViewId();
                        }
                    }
                }
            }

            PrePrepareMessage prePrepareMessage = new PrePrepareMessage();
            prePrepareMessage.setViewId(newViewID);
            prePrepareMessage.setSequenceNumber(n);
            if (highestViewID >= MIN_VIEW_ID) {
            // TODO    prePrepareMessage.setMessageSignature(/* TODO */);
            } else {
            // TODO    prePrepareMessage.setMessageSignature( /* TODO set as noop */);
            }
            // TODO prePrepareMessage.setTransactionDigest( /* TODO */ );
            prePrepareMessages.add(prePrepareMessage);
        }
        return prePrepareMessages;
    }

    @Override
    public synchronized void startViewChange(ViewChangeMessage message) throws TException {
        if (message.isSetNewViewID()) {
            int newViewID = message.getNewViewID();
            if (newViewID > configProvider.getViewID()) { // can only move to a higher view
                // TODO: verify validity
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
                    for (final GroupMember<PBFTCohort.Client> groupMember : groupMembers)
                        if (groupMember.getReplicaID() != replicaID) {
                            final NewViewMessage newViewMessage = new NewViewMessage();
                            newViewMessage.setNewViewID(newViewID);
                            newViewMessage.setViewChangeMessages(viewChangeMessages.get(newViewID));
                            newViewMessage.setPrePrepareMessages(
                                    createPrePrepareForCurrentSeqno(newViewID, viewChangeMessages.get(newViewID)));
                            pool.execute(new Runnable() {
                                public void run() {
                                    try {
                                        groupMember.getThriftConnection().approveViewChange(newViewMessage);
                                    } catch (InvocationTargetException e) {
                                        // ignore
                                    } catch (TException e) {
                                        // ignore
                                    } catch (InstantiationException e) {
                                        // ignore
                                    } catch (IllegalAccessException e) {
                                        // ignore
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
        // change to new view

        // clear old entries for old views out from viewChangeMessages
    }
}
