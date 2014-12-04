package gameengine;

import common.LogListener;
import common.Transaction;
import statemachine.Operation;

/**
 * Created by leo on 12/3/14.
 */
public interface GameEngine<T> extends LogListener<Operation<T>> {
    public void requestCommit(Operation<T> transaction);
}
