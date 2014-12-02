package gameengine;

import statemachine.InvalidStateMachineOperationException;
import statemachine.Operation;
import statemachine.StateMachine;

/**
 * Created by andrew on 11/30/14.
 */
public class ChineseCheckersStateMachine implements StateMachine<ChineseCheckersState, Operation<ChineseCheckersState>> {

    private ChineseCheckersState state;

//    public ChineseCheckersStateMachine() {
//        this.state = new ChineseCheckersState();
//    }

    public ChineseCheckersStateMachine(ChineseCheckersState state) {
        this.state = state;
    }

    @Override
    public void applyOperation(Operation<ChineseCheckersState> op) throws InvalidStateMachineOperationException {
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
