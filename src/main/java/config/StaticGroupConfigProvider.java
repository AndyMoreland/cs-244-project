package config;

import com.google.common.collect.Sets;
import com.sun.istack.internal.Nullable;

import java.util.Set;

/**
 * Created by andrew on 11/27/14.
 * Simple group membership provider that doesn't take into account dynamic network conditions or change.
 */
public class StaticGroupConfigProvider<T extends org.apache.thrift.TServiceClient> implements GroupConfigProvider<T> {
    private final GroupMember<T> me;
    private Set<GroupMember<T>> members;
    private int viewID;

    public StaticGroupConfigProvider(GroupMember<T> me, Set<GroupMember<T>> members, int viewID) {
        this.me = me;
        this.members = members;
        assert (this.members != null);
        this.viewID = viewID;
        assert(getGroupMember((viewID % members.size()) + 1)!= null);
    }

    @Override
    public synchronized void setViewID(int viewID) {
        this.viewID = viewID;
        assert(getLeader() != null);
    }

    @Override
    public synchronized int getViewID() {
        return this.viewID;
    }

    @Override
    public int getQuorumSize() {
        return members.size() - (members.size()-1)/3;
    }

    @Override
    public Set<GroupMember<T>> getOtherGroupMembers() {
        Set<GroupMember<T>> otherMembers = Sets.newHashSet(members);
        otherMembers.remove(me);

        return otherMembers;
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
    public synchronized GroupMember getLeader() { return getGroupMember((viewID % members.size()) + 1); }


    @Override
    public GroupMember getMe(){ return me; }

    @Override
    public String toString() {
        return "StaticGroupConfigProvider{" +
                "members=" + members +
                ", viewID=" + viewID +
                ", leader=" + getLeader() +
                '}';
    }
}
