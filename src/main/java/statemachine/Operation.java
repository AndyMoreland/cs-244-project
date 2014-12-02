package statemachine;

import PBFT.TOperation;

/**
 * Created by andrew on 11/30/14.
 */
public interface Operation<T> {
    void apply(T state);
    boolean isValid(T state);
    void undo(T state);

    TOperation serialize();
}
