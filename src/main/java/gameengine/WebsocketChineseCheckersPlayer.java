package gameengine;

import common.LogListener;
import common.Transaction;
import gameengine.operations.AddPlayer;
import gameengine.operations.KickPlayer;
import gameengine.operations.MovePiece;
import gameengine.operations.PlayerLeave;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import statemachine.Operation;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * Created by sctu on 12/7/14.
 */
public class WebsocketChineseCheckersPlayer extends WebSocketServer implements LogListener<Operation<ChineseCheckersState>> {
    private static Logger LOG = LogManager.getLogger(WebsocketChineseCheckersPlayer.class);

    private ChineseCheckersGameEngine gameEngine;
    public WebsocketChineseCheckersPlayer(InetSocketAddress address, ChineseCheckersGameEngine gameEngine) {
        super(address);
        this.gameEngine = gameEngine;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        // player join
        gameEngine.requestCommit(new AddPlayer());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        // player leave
        gameEngine.requestCommit(new PlayerLeave());
    }

    /* We expect to get the moves as
     * MOVE,2,3,4,5
     * KICK,1
     */
    @Override
    public void onMessage(WebSocket webSocket, String s) {
        String[] strParts = s.split(",");
        if (strParts.length == 5 && strParts[0].equals("MOVE")) {
            int startPointq, startPointr, endPointq, endPointr;
            try {
                startPointq = Integer.parseInt(strParts[1]);
                startPointr = Integer.parseInt(strParts[2]);
                endPointq = Integer.parseInt(strParts[3]);
                endPointr = Integer.parseInt(strParts[4]);
                gameEngine.requestCommit(new MovePiece(
                        gameEngine.configProvider.getMe().getReplicaID(),
                        new HexPoint(startPointq, startPointr),
                        new HexPoint(endPointq, endPointr)));
            } catch (NumberFormatException e) {
                LOG.error("Unrecognized MOVE coordinates: " + s);
            }
        } else if (strParts.length == 2 && strParts[0].equals("KICK")) {
            try {
                gameEngine.requestCommit(new KickPlayer());
            } catch (NumberFormatException e) {
                LOG.error("Cannot KICK " + strParts[0]);
            }
        } else {
            LOG.error("Unrecognized move: " + s);
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        LOG.error(e);
    }

    @Override
    public void notifyOnCommit(Transaction<Operation<ChineseCheckersState>> transaction) throws Exception {
        // TODO: Make sure all Operations have a reasonable toString
        this.sendToAll(transaction.getValue().toString());
    }

    /* Taken from the websocket example code
     */
    public void sendToAll(String text) {
        Collection<WebSocket> con = connections();
        synchronized (con) {
            for(WebSocket c : con) {
                c.send(text);
            }
        }
    }
}
