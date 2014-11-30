package gameengine;

import java.util.List;

/**
 * Created by andrew on 11/30/14.
 */
public class ChineseCheckersState {
    private ChineseCheckersBoard board = new ChineseCheckersBoard();
    private final List<Player> players;
    private int currentPlayerIndex;

    /**
     * Constructs the state for a fresh chinese checkers game
     * @param players
     */
    public ChineseCheckersState(List<Player> players) {
        this.players = players;
        currentPlayerIndex = 0;
    }

    /**
     * Designed for entering an in-progress game
     * @param board
     * @param players
     * @param currentPlayerIndex
     */
    public ChineseCheckersState(ChineseCheckersBoard board, List<Player> players, int currentPlayerIndex) {
        this.board = board;
        this.players = players;
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

}
