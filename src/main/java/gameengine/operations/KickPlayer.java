package gameengine.operations;

import PBFT.TChineseCheckersOperation;
import PBFT.TOperation;
import gameengine.ChineseCheckersState;

/**
 * Created by andrew on 11/30/14.
 */
public class KickPlayer implements statemachine.Operation<ChineseCheckersState> {
    private int replicaId;

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
        TOperation tOperation = new TOperation();
        tOperation.setArguments(new Integer(replicaId).toString());
        tOperation.setOperationType(TChineseCheckersOperation.KICK_PLAYER.getValue());
        return tOperation;
    }
}
