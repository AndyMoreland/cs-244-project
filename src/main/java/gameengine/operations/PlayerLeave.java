package gameengine.operations;

import PBFT.TOperation;
import gameengine.ChineseCheckersState;

/**
 * Created by sctu on 12/8/14.
 */
public class PlayerLeave implements statemachine.Operation<ChineseCheckersState> {
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
