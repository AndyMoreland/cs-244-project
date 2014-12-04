package gameengine;

import com.google.common.collect.Maps;
import common.Transaction;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import statemachine.Operation;


/**
 * Created by sctu on 12/4/14.
 */
public class BenchmarkingGameEngine implements GameEngine<ChineseCheckersState> {

    private long startTime;
    private static Logger LOG = LogManager.getLogger(BenchmarkingGameEngine.class);
    // runner should call this
    @Override
    public void requestCommit(Operation<ChineseCheckersState> transaction) {
        this.startTime = System.currentTimeMillis());

    }

    // log should call this
    @Override
    public void notifyOnCommit(Transaction<Operation<ChineseCheckersState>> transaction) throws Exception {
        LOG.error("Time spent on last move: " + (System.currentTimeMillis() - startTime));
    }
}
