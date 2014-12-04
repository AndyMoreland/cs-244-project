package gameengine;

import common.Transaction;
import config.GroupConfigProvider;
import statemachine.InvalidStateMachineOperationException;
import statemachine.Operation;

/**
 * Created by leo on 12/3/14.
 */
public class ChineseCheckersGameEngine implements GameEngine<ChineseCheckersState> {

    private final GroupConfigProvider configProvider;
    private ChineseCheckersStateMachine stateMachine;

    public ChineseCheckersGameEngine(GroupConfigProvider configProvider){
        this.configProvider = configProvider;
        this.stateMachine = new ChineseCheckersStateMachine(ChineseCheckersState.buildGameForGroupMembers(configProvider.getGroupMembers()));
    }
    @Override
    public void notifyOnCommit(Transaction<Operation<ChineseCheckersState>> transaction) throws InvalidStateMachineOperationException {
        this.stateMachine.applyOperation(transaction.getValue());
    }

    @Override
    public void requestCommit(Operation<ChineseCheckersState> transaction) {

    }

}
