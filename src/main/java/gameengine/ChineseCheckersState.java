package gameengine;

import java.util.List;

/**
 * Created by andrew on 11/30/14.
 */
public class ChineseCheckersState {
    private ChineseCheckersBoard board;
    private final List<Player> players;
    private int currentPlayerIndex;

    /**
     * Constructs the state for a fresh chinese checkers game
     * @param players
     */
    public ChineseCheckersState(List<Player> players) {
        while(players.size() < 6) players.add(Player.makeInactivePlayer());
        this.board = new ChineseCheckersBoard(players);
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

    public ChineseCheckersSpot getSpot(HexPoint pt) { return this.board.getSpot(pt); }

    // public List<MovePiece> getAvailableMovesForCurrentPlayer

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextActivePlayer() {
        // Will infinite loop if all players inactive
        do {
            this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.players.size();
        } while(!this.players.get(this.currentPlayerIndex).isActive());
    }
}
