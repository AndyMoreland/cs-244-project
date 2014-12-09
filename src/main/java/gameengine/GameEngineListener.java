package gameengine;


import statemachine.Operation;

/**
 * Created by sctu on 12/8/14.
 */
public interface GameEngineListener<T> {
    public abstract void notifyOnSuccessfulApply(Operation<T> operation);
}
