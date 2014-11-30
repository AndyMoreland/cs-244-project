package config;

import java.util.Set;

/**
 * Created by andrew on 11/27/14.
 */
public interface GroupConfigProvider {

    void setViewID(int viewID);
    int getViewID();
    int getQuorumSize();

    Set<GroupMember> getGroupMembers();

    GroupMember getLeader();

    void setLeader(GroupMember leader);


}
