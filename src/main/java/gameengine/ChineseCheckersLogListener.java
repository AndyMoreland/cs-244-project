package gameengine;

import common.LogListener;
import common.Transaction;
import statemachine.Operation;

/**
 * Created by andrew on 12/2/14.
 */
public class ChineseCheckersLogListener implements LogListener<Operation<ChineseCheckersState>> {

    private ChineseCheckersStateMachine chineseCheckersStateMachine;

    public ChineseCheckersLogListener(ChineseCheckersStateMachine chineseCheckersStateMachine) {
        this.chineseCheckersStateMachine = chineseCheckersStateMachine;
    }

    @Override
    public void notifyOnCommit(Transaction<Operation<ChineseCheckersState>> transaction) throws Exception {
        chineseCheckersStateMachine.applyOperation(transaction.getValue());
    }
}
