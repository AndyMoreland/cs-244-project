package config;

import java.util.Set;

/**
 * Created by andrew on 11/27/14.
 */
public interface GroupConfigProvider<T extends org.apache.thrift.TServiceClient> {
    Set<GroupMember<T>> getGroupMembers();

    GroupMember<T> getLeader();

    void setLeader(GroupMember<T> leader);
}
