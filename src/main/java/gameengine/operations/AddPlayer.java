package gameengine.operations;

import PBFT.Operation;
import gameengine.ChineseCheckersState;

/**
 * Created by andrew on 11/30/14.
 */
public class AddPlayer implements ChineseCheckersOperation {
    @Override
    public void apply(ChineseCheckersState state) {
        
    }

    @Override
    public Operation serialize() {
        return null;
    }
}
