package gameengine.operations;

import PBFT.Operation;
import gameengine.ChineseCheckersSpot;
import gameengine.ChineseCheckersState;
import gameengine.HexDirection;
import gameengine.HexPoint;

/**
 * Created by andrew on 11/30/14.
 */
public class MovePiece implements ChineseCheckersOperation {

    private final HexPoint start;
    private final HexPoint end;

    public MovePiece(HexPoint start, HexPoint end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public void apply(ChineseCheckersState state) {
        boolean lastMove = start.equals(end) || start.isNeighbor(end);
        state.getSpot(start).moveOccupantTo(state.getSpot(end));
        if(lastMove) state.nextActivePlayer();
    }

    @Override
    public boolean isValid(ChineseCheckersState state) {
        ChineseCheckersSpot startSpot = state.getSpot(start);
        ChineseCheckersSpot endSpot = state.getSpot(end);


        if(startSpot == null || endSpot == null) return false;  // Both spots must be within board bounds
        if(startSpot.getOccupant() == null) return false;       // Must move from full spot

        HexPoint moveDelta = end.subtract(start);
        if (moveDelta.isZero()) return true;                    // Player passes

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
    public void undo(ChineseCheckersState state) {
        
    }

    @Override
    public Operation serialize() {
        return null;
    }
}
