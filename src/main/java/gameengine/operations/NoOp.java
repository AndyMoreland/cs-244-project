package gameengine.operations;

import PBFT.TOperation;
import gameengine.ChineseCheckersState;

/**
 * Created by sctu on 11/30/14.
 */
public class NoOp implements statemachine.Operation<ChineseCheckersState> {
    @Override
    public void apply(ChineseCheckersState state) {

    }

    @Override
    public boolean isValid(ChineseCheckersState state) {
        return false;
    }

    @Override
    public void undo(ChineseCheckersState state) {

    }

    @Override
    public TOperation serialize() {
        return null;
    }
}
