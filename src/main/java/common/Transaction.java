package common;

/**
 * Created by andrew on 11/27/14.
 */
public class Transaction<T> {
    private int id;
    private final int targetIndex;
    private final T value;
    private boolean committed;

    public Transaction(int id, int targetIndex, T value) {
        this.id = id;
        this.targetIndex = targetIndex;
        this.value = value;
        this.committed = false;
    }

    public void commit() {
        committed = true;
    }
}
