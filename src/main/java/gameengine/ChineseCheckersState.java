package gameengine;

import PBFT.PBFTCohort;
import com.google.common.collect.Lists;
import config.GroupMember;

import java.util.List;
import java.util.Set;

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

    public void prevActivePlayer() {
        // Will infinite loop if all players inactive
        do {
            this.currentPlayerIndex = (this.currentPlayerIndex - 1) % this.players.size();
        } while(!this.players.get(this.currentPlayerIndex).isActive());
    }

    public static ChineseCheckersState buildGameForGroupMembers(Set<GroupMember<PBFTCohort.Client>> groupMembers) {
        List<Player> players = Lists.newArrayList();

        for (GroupMember member: groupMembers) {
            players.add(new Player(member.getName(), member.getReplicaID()));
        }

        return new ChineseCheckersState(players);
    }
}
