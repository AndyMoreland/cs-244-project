package gameengine;

import gameengine.operations.MovePiece;
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
public class WebsocketChineseCheckersPlayer extends WebSocketServer implements GameEngineListener<ChineseCheckersState> {
    private static Logger LOG = LogManager.getLogger(WebsocketChineseCheckersPlayer.class);

    private GameEngine<ChineseCheckersState> gameEngine;
    private int playerID;
    public WebsocketChineseCheckersPlayer(InetSocketAddress address, GameEngine<ChineseCheckersState> gameEngine, int replicaID) {
        super(address);
        this.gameEngine = gameEngine;
        this.playerID = replicaID;
        this.gameEngine.addListener(this); // so we can be notified when moves are successfully applied
        LOG.info("Ready for client connection at " + address);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    /* We expect to get the moves as
     * MOVE,2,3,4,5
     */
    @Override
    public void onMessage(WebSocket webSocket, String s) {
        LOG.info("Got a message from frontend: " + s);
        String[] strParts = s.split(",");
        if (strParts.length == 5 && strParts[0].equals("MOVE")) {
            int startPointq, startPointr, endPointq, endPointr;
            try {
                startPointq = Integer.parseInt(strParts[1]);
                startPointr = Integer.parseInt(strParts[2]);
                endPointq = Integer.parseInt(strParts[3]);
                endPointr = Integer.parseInt(strParts[4]);
                gameEngine.requestCommit(new MovePiece(
                        playerID,
                        new HexPoint(startPointq, startPointr),
                        new HexPoint(endPointq, endPointr)));
            } catch (NumberFormatException e) {
                LOG.error("Unrecognized MOVE coordinates: " + s);
            }
        } else {
            LOG.error("Unrecognized move: " + s);
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        LOG.error(e);
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

    @Override
    public void notifyOnSuccessfulApply(Operation<ChineseCheckersState> operation) {
        // TODO: Make sure all Operations have a reasonable toString
        this.sendToAll(operation.toString());
    }
}
