package config;

import java.util.Set;

/**
 * Created by andrew on 11/27/14.
 */
public interface GroupConfigProvider<T extends org.apache.thrift.TServiceClient> {

    void setViewID(int viewID);
    int getViewID();
    int getQuorumSize();

    Set<GroupMember<T>> getOtherGroupMembers();
    Set<GroupMember<T>> getGroupMembers();
    GroupMember<T> getGroupMember(int replicaID);

    GroupMember<T> getLeader();

    GroupMember getMe();
}
