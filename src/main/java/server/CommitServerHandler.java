package server;

import TwoPhaseCommit.CommitServer;
import TwoPhaseCommit.Vote;
import config.GroupConfigProvider;
import org.apache.thrift.TException;

/**
 * Created by andrew on 11/27/14.
 */
public class CommitServerHandler implements CommitServer.Iface {
    private GroupConfigProvider configProvider;

    public CommitServerHandler(GroupConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    @Override
    public int requestMessage(int index, int value) throws TException {
        return 0;
    }

    @Override
    public Vote requestOutcome(int index) throws TException {
        return null;
    }
}
