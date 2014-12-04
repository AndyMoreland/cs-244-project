package gameengine;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import statemachine.InvalidStateMachineOperationException;
import statemachine.Operation;
import statemachine.StateMachine;

/**
 * Created by andrew on 11/30/14.
 */
public class ChineseCheckersStateMachine implements StateMachine<ChineseCheckersState, Operation<ChineseCheckersState>> {
    private static Logger LOG = LogManager.getLogger(ChineseCheckersStateMachine.class);

    private ChineseCheckersState state;

    public ChineseCheckersStateMachine(ChineseCheckersState state) {
        this.state = state;
    }

    @Override
    synchronized public void applyOperation(Operation<ChineseCheckersState> op) throws InvalidStateMachineOperationException {
        LOG.info("Applying operation: " + op);

        if (!op.isValid(this.getState())) { throw new InvalidStateMachineOperationException(); }

        op.apply(this.getState());
    }

    synchronized public ChineseCheckersState getState() {
        return state;
    }

    synchronized public void setState(ChineseCheckersState newState) {
        state = newState;
    }
}
