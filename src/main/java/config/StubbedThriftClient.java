package config;

import PBFT.*;
import org.apache.thrift.TException;

/**
 * Created by leo on 12/1/14.
 */
public class StubbedThriftClient implements PBFTCohort.Iface {

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

    @Override
    public void startViewChange(ViewChangeMessage message) throws TException {

    }

    @Override
    public void approveViewChange(NewViewMessage message) throws TException {

    }

    @Override
    public Transaction getTransaction(AskForTransaction message) throws TException {
        return null;
    }

    @Override
    public void ping() throws TException {

    }
}
