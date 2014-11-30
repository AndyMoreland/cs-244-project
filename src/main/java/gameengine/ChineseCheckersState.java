package gameengine;

import java.util.List;

/**
 * Created by andrew on 11/30/14.
 */
public class ChineseCheckersState {
    private ChineseCheckersBoard board = new ChineseCheckersBoard();
    private final List<Player> players;
    private int currentPlayerIndex;

    public ChineseCheckersState(List<Player> players) {
        this.players = players;
        currentPlayerIndex = 0;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

}
