package statemachine;

/**
 * Created by andrew on 11/30/14.
 */
public interface Operation<T> {
    void apply(T state);
    boolean isValid(T state);

    PBFT.Operation serialize();
}
