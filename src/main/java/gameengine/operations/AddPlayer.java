package gameengine.operations;

import PBFT.TOperation;
import gameengine.ChineseCheckersState;

/**
 * Created by andrew on 11/30/14.
 */
public class AddPlayer implements statemachine.Operation<ChineseCheckersState> {
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
