package server;

import PBFT.*;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import config.GroupConfigProvider;
import config.GroupMember;
import org.apache.thrift.TException;

import java.util.Map;
import java.util.Set;

/**
 * Created by andrew on 11/27/14.
 */
public class PBFTCohortHandler implements PBFTCohort.Iface {
    private GroupConfigProvider configProvider;
    private Map<Integer,Set<ViewChangeMessage>> viewChangeMessages;
    private int replicaID;

    public PBFTCohortHandler(GroupConfigProvider configProvider, int replicaID) {
        this.configProvider = configProvider;
        viewChangeMessages = Maps.newHashMap();
        this.replicaID = replicaID;
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

    @Override
    public void startViewChange(ViewChangeMessage message) throws TException {
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
                if (configProvider.getLeader().getReplicaID()==replicaID && viewChangeMessages.get(newViewID).size()>configProvider.getQuorumSize()) {
                    // multicast NEW-VIEW message
                    Set<GroupMember> groupMembers = configProvider.getGroupMembers();
                    for (GroupMember groupMember : groupMembers) {
                        if (groupMember.getReplicaID() != replicaID) {
                            // TODO
                        }
                    }

                }
            }
        }
    }

    @Override
    public void approveViewChange(NewViewMessage message) throws TException {

        // clear old entries for old views out from viewChangeMessages
    }
}
