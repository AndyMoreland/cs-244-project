package gameengine;

import common.Transaction;
import config.GroupConfigProvider;
import gameengine.operations.NoOp;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import statemachine.InvalidStateMachineOperationException;
import statemachine.Operation;


/**
 * Created by sctu on 12/4/14.
 */
public class BenchmarkingGameEngine extends ChineseCheckersGameEngine {

    private long startTime;
    private static Logger LOG = LogManager.getLogger(BenchmarkingGameEngine.class);

    public BenchmarkingGameEngine(GroupConfigProvider configProvider) {
        super(configProvider);
    }

    // runner should call this
    @Override
    public void requestCommit(Operation<ChineseCheckersState> transaction) {
        this.startTime = System.currentTimeMillis();
        super.requestCommit(transaction);
    }

    // log should call this
    @Override
    public void notifyOnCommit(Transaction<Operation<ChineseCheckersState>> transaction) throws InvalidStateMachineOperationException {
        super.notifyOnCommit(transaction);
        LOG.warn("Committed message on server: " + configProvider.getMe().getName());
        LOG.warn("The transaction's replica ID is: " + transaction.getReplicaId());

        if (transaction.getReplicaId() == configProvider.getMe().getReplicaID()) {
            LOG.error("Time spent on last move: " + (System.currentTimeMillis() - startTime));
        }

        if ((transaction.getReplicaId() % configProvider.getGroupMembers().size()) + 1 == configProvider.getMe().getReplicaID()) {
            requestCommit(new NoOp());
        }
    }
}

