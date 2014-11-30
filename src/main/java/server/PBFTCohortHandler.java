package server;

import PBFT.*;
import config.GroupConfigProvider;
import org.apache.thrift.TException;

/**
 * Created by andrew on 11/27/14.
 */
public class PBFTCohortHandler implements PBFTCohort.Iface {
    private GroupConfigProvider configProvider;

    public PBFTCohortHandler(GroupConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    @Override
    public void prePrepare(PrePrepareMessage message, Transaction transaction) throws TException {

    }

    @Override
    public void prepare(PrepareMessage message) throws TException {

    }

    @Override
    public void commit(CommitMessage message) throws TException {

    }

    @Override
    public void checkpoint(CheckpointMessage message) throws TException {

    }
}
