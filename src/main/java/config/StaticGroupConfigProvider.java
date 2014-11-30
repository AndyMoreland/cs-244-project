package config;

import java.util.Set;

/**
 * Created by andrew on 11/27/14.
 * Simple group membership provider that doesn't take into account dynamic network conditions or change.
 */
public class StaticGroupConfigProvider implements GroupConfigProvider {
    private Set<GroupMember> members;
    private GroupMember leader;

    public StaticGroupConfigProvider(GroupMember leader, Set<GroupMember> members) {
        assert (leader != null);
        assert (members != null);

        this.leader = leader;
        this.members = members;
    }

    @Override
    public Set<GroupMember> getGroupMembers() {
        return members;
    }

    @Override
    public GroupMember getLeader() { return leader; }

    @Override
    public void setLeader(GroupMember leader) {
        assert (members.contains(leader));

        this.leader = leader;
    }
}
