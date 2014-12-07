package statemachine;

import common.StateMachineListener;

/**
 * Created by andrew on 11/30/14.
 */
public interface StateMachine<StateType, OperationType extends Operation<StateType>> {

    void applyOperation(OperationType op) throws InvalidStateMachineOperationException;

    StateType getState();

    void setState(StateType newState);

    void addCheckpointListener(StateMachineListener listener);
}
