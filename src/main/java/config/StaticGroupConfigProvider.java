package config;

import com.sun.istack.internal.Nullable;

import java.util.Set;

/**
 * Created by andrew on 11/27/14.
 * Simple group membership provider that doesn't take into account dynamic network conditions or change.
 */
public class StaticGroupConfigProvider<T extends org.apache.thrift.TServiceClient> implements GroupConfigProvider<T> {
    private Set<GroupMember<T>> members;
    private int viewID;
    private GroupMember<T> leader;

    public StaticGroupConfigProvider(GroupMember<T> leader, Set<GroupMember<T>> members, int viewID) {
        assert (leader != null);
        assert (members != null);

        this.leader = leader;
        this.members = members;
        this.viewID = viewID;
    }

    @Override
    public synchronized void setViewID(int viewID) {
        this.viewID = viewID;
    }

    @Override
    public synchronized int getViewID() {
        return this.viewID;
    }

    @Override
    public int getQuorumSize() {
        return (members.size() + 1) - (members.size())/3;
    }

    @Override
    public Set<GroupMember<T>> getGroupMembers() {
        return members;
    }

    @Override
    @Nullable
    public GroupMember getGroupMember(int replicaID) {
        for (GroupMember member : members) {
            if (member.getReplicaID() == replicaID) {
                return member;
            }
        }
        return null;
    }

    @Override
    public GroupMember getLeader() { return leader; }

    @Override
    public void setLeader(GroupMember leader) {
        assert (members.contains(leader));

        this.leader = leader;
    }

    @Override
    public String toString() {
        return "StaticGroupConfigProvider{" +
                "members=" + members +
                ", viewID=" + viewID +
                ", leader=" + leader +
                '}';
    }
}
