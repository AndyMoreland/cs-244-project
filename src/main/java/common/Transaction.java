package common;

import PBFT.Viewstamp;
import gameengine.ChineseCheckersOperationFactory;
import gameengine.ChineseCheckersState;
import statemachine.Operation;

/**
 * Created by andrew on 11/27/14.
 */
public class Transaction<T> {
    private Viewstamp id;
    private final int targetIndex;
    private final T value;
    private boolean prepared;
    private boolean committed;
    private Viewstamp viewStamp;

    public Transaction(Viewstamp id, int targetIndex, T value) {
        this.id = id;
        this.targetIndex = targetIndex;
        this.value = value;
        this.committed = false;
    }

    public void prepare() { prepared = true; }
    public void commit() {
        committed = true;
    }

    public boolean isPrepared() { return prepared; }
    public boolean isCommitted() { return committed; }

    public Viewstamp getViewstamp() {
        return id;
    }

    public static Transaction<Operation<ChineseCheckersState>> getTransactionForPBFTTransaction(PBFT.Transaction transaction) {
        common.Transaction<statemachine.Operation<ChineseCheckersState>> commonTransaction = new Transaction<Operation<ChineseCheckersState>>(
                transaction.viewstamp,
                transaction.viewstamp.getSequenceNumber(),
                ChineseCheckersOperationFactory.hydrate(transaction.getOperation()));

        return commonTransaction;
    }
}
