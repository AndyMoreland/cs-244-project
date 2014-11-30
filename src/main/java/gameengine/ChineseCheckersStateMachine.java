package gameengine;

import gameengine.operations.ChineseCheckersOperation;
import statemachine.InvalidStateMachineOperationException;
import statemachine.StateMachine;

/**
 * Created by andrew on 11/30/14.
 */
public class ChineseCheckersStateMachine implements StateMachine<ChineseCheckersState, ChineseCheckersOperation> {


    private ChineseCheckersState state;

    @Override
    public void applyOperation(ChineseCheckersOperation op) throws InvalidStateMachineOperationException {
        if (operationIsIllegal(op)) { throw new InvalidStateMachineOperationException(); }

        op.apply(this.getState());
    }

    public boolean operationIsIllegal(ChineseCheckersOperation op) {
        return false;
    }

    public ChineseCheckersState getState() {
        return state;
    }

    public void setState(ChineseCheckersState newState) {
        state = newState;
    }
}
