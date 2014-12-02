package gameengine;

import PBFT.TChineseCheckersOperation;
import PBFT.TOperation;
import gameengine.operations.AddPlayer;
import gameengine.operations.KickPlayer;
import gameengine.operations.MovePiece;
import gameengine.operations.NoOp;
import statemachine.Operation;

/**
 * Created by andrew on 11/30/14.
 */
public class ChineseCheckersOperationFactory {
    public static Operation<ChineseCheckersState> hydrate(TOperation op) {
        TChineseCheckersOperation type = TChineseCheckersOperation.findByValue(op.getOperationType());

        switch (type) {
            case MOVE_PIECE:
                return new MovePiece(-1, 1,1,1,1);
            case KICK_PLAYER:
                return new KickPlayer();
            case ADD_PLAYER:
                return new AddPlayer();
            case NO_OP:
                return new NoOp();
        }
        return null;
    }
}
