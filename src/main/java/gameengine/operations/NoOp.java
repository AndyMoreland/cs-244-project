package gameengine.operations;

import PBFT.TChineseCheckersOperation;
import PBFT.TOperation;
import gameengine.ChineseCheckersState;

/**
 * Created by sctu on 11/30/14.
 */
public class NoOp implements statemachine.Operation<ChineseCheckersState> {
    public int getA() {
        return a;
    }

    public int a = 3;

    @Override
    public void apply(ChineseCheckersState state) {

    }

    @Override
    public boolean isValid(ChineseCheckersState state) {
        return true;
    }

    @Override
    public void undo(ChineseCheckersState state) {

    }

    @Override
    public TOperation serialize() {
        TOperation tOperation = new TOperation();
        tOperation.setArguments("");
        tOperation.setOperationType(TChineseCheckersOperation.NO_OP.getValue());
        return tOperation;
    }
}
