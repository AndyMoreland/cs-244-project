package gameengine;

import gameengine.operations.AddPlayer;
import gameengine.operations.ChineseCheckersOperation;
import gameengine.operations.KickPlayer;
import gameengine.operations.MovePiece;

/**
 * Created by andrew on 11/30/14.
 */
public class ChineseCheckersOperationFactory {
    public static ChineseCheckersOperation hydrate(PBFT.Operation op) {
        PBFT.ChineseCheckersOperation type = PBFT.ChineseCheckersOperation.findByValue(op.getOperationType());

        switch (type) {
            case MOVE_PIECE:
                return new MovePiece(1,1,1,1);
            case KICK_PLAYER:
                return new KickPlayer();
            case ADD_PLAYER:
                return new AddPlayer();
        }
        return null;
    }
}
