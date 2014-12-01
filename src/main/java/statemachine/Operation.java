package statemachine;

/**
 * Created by andrew on 11/30/14.
 */
public interface Operation<T> {
    void apply(T state);
    void undo(T state);

    PBFT.Operation serialize();
}
