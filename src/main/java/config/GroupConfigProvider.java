package config;

import java.util.List;

/**
 * Created by andrew on 11/27/14.
 */
public interface GroupConfigProvider {
    List<GroupMember> getGroupMembers();
}
