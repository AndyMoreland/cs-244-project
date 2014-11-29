package client;

import TwoPhaseCommit.CommitClient;
import TwoPhaseCommit.Vote;
import org.apache.thrift.TException;

/**
 * Created by andrew on 11/27/14.
 */
public class CommitClientHandler implements CommitClient.Iface {
    @Override
    public Vote prepare(int index, int value) throws TException {
        return null;
    }

    @Override
    public void acceptOutcome(int index, Vote outcome) throws TException {

    }
}
