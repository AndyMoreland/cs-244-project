package gameengine;

import com.google.common.collect.Maps;
import common.Transaction;
import config.GroupConfigProvider;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import server.PBFTServerInstance;
import statemachine.InvalidStateMachineOperationException;
import statemachine.Operation;


/**
 * Created by sctu on 12/4/14.
 */
public class BenchmarkingGameEngine extends ChineseCheckersGameEngine {

    private long startTime;
    private static Logger LOG = LogManager.getLogger(BenchmarkingGameEngine.class);
    private PBFTServerInstance serverInstance;

    public BenchmarkingGameEngine(GroupConfigProvider configProvider, PBFTServerInstance serverInstance) {
        super(configProvider);
        this.serverInstance = serverInstance;
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
        LOG.error("Time spent on last move: " + (System.currentTimeMillis() - startTime));
        serverInstance.notifyOnNextTurn();
    }
}
