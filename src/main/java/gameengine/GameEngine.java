package gameengine;

import common.LogListener;
import common.Transaction;
import statemachine.InvalidStateMachineOperationException;
import statemachine.Operation;
import statemachine.StateMachine;

/**
 * Created by leo on 12/3/14.
 */
public interface GameEngine<T> extends LogListener<Operation<T>> {
    public void requestCommit(Operation<T> transaction);
    public StateMachine<T, Operation<T>> getStateMachine();
    public void addListener(GameEngineListener<T> listener);
}
