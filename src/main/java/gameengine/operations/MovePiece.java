package gameengine.operations;

import PBFT.Operation;
import gameengine.*;

/**
 * Created by andrew on 11/30/14.
 */
public class MovePiece implements ChineseCheckersOperation {

    private final HexPoint start;
    private final HexPoint end;
    private final int replicaID;

    public MovePiece(int replicaID, HexPoint start, HexPoint end) {
        this.replicaID = replicaID;
        this.start = start;
        this.end = end;
    }

    public MovePiece(int replicaID, int q1, int r1, int q2, int r2) {
        this.replicaID = replicaID;
        this.start = new HexPoint(q1, r1);
        this.end = new HexPoint(q2, r2);
    }

    @Override
    public void apply(ChineseCheckersState state) {
        boolean lastMove = start.equals(end) || start.isNeighbor(end);
        state.getSpot(start).moveOccupantTo(state.getSpot(end));
        if(lastMove) state.nextActivePlayer();
    }

    @Override
    public void undo(ChineseCheckersState state) {
        boolean lastMove = start.equals(end) || start.isNeighbor(end);
        state.getSpot(end).moveOccupantTo(state.getSpot(start));
        if(lastMove) state.prevActivePlayer();
    }

    @Override
    public boolean isValid(ChineseCheckersState state) {
        ChineseCheckersSpot startSpot = state.getSpot(start);
        ChineseCheckersSpot endSpot = state.getSpot(end);

        if(startSpot == null || endSpot == null) return false;     // Both spots must be within board bounds
        if(startSpot.getOccupant() == null) return false;          // Must move from full spot

        Player current = state.getCurrentPlayer();
        if(this.replicaID != current.getReplicaId()) return false; // Only current player may request a move
        if(startSpot.getOccupant() != current) return false;       // And player can only move own piece

        HexPoint moveDelta = end.subtract(start);
        if (moveDelta.isZero()) return true;                       // Player passes

        // If not passing must move to empty spot
        if(endSpot.getOccupant() != null) return false;

        // Step to adjacent hex
        if (end.isNeighbor(start)) return true;

        // Jump over hex
        for(HexDirection dir : HexDirection.values()){
            HexPoint dirOffset = dir.getOffset();

            // If jump in this direction: allowed iff middle spot not empty
            if(dirOffset.scale(2).equals(moveDelta)){
                HexPoint midpoint = start.add(dirOffset);
                return !state.getSpot(midpoint).isEmpty();
            }
        }

        return false;
    }

    @Override
    public Operation serialize() {
        return null;
    }
}
