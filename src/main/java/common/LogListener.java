package common;

/**
 * Created by andrew on 12/2/14.
 */
public interface LogListener<T> {
    public void notifyOnCommit(Transaction<T> transaction) throws Exception;
}
