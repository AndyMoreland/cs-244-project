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
        if (!op.isValid(this.getState())) { throw new InvalidStateMachineOperationException(); }

        op.apply(this.getState());
    }

    public ChineseCheckersState getState() {
        return state;
    }

    public void setState(ChineseCheckersState newState) {
        state = newState;
    }
}
