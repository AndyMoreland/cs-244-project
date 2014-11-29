package config;

import java.util.List;

/**
 * Created by andrew on 11/27/14.
 * Simple group membership provider that doesn't take into account dynamic network conditions or change.
 */
public class StaticGroupConfigProvider implements GroupConfigProvider {
    private List<GroupMember> members;

    public StaticGroupConfigProvider(List<GroupMember> members) {
        this.members = members;
    }

    @Override
    public List<GroupMember> getGroupMembers() {
        return members;
    }
}
