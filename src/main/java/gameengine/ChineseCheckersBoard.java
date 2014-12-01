package gameengine;

import java.util.HashMap;
import java.util.List;

/**
 * Created by andrew on 11/30/14.
 */
public class ChineseCheckersBoard {

    private static final int[][] BOARD_COORDS = {
        {0,0},{-1,1},{-1,0},{0,-1},{1,-1},{1,0},{0,1},{-2,2},{-1,2},
        {0,2},{1,1},{2,0},{2,-1},{2,-2},{1,-2},{0,-2},{-1,-1},{-2,0},
        {-2,1},{-3,2},{-3,1},{-3,0},{-2,-2},{-2,-1},{0,-3},{-1,-2},
        {1,-3},{2,-3},{3,-3},{3,-2},{3,-1},{3,0},{2,1},{1,2},{0,3},{-1,3},
        {-2,3},{-3,3},{-3,-1},{-4,0},{-4,1},{-4,2},{-4,3},{-4,4},{-3,4},
        {-2,4},{-1,4},{0,4},{-1,-3},{0,-4},{1,-4},{2,-4},{3,-4},{4,-4},
        {4,-3},{4,-2},{4,-1},{4,0},{3,1},{2,2},{1,3},{2,-5},{3,-5},{3,-6},
        {5,-3},{6,-3},{5,-2},{3,2},{3,3},{2,3},{-2,5},{-3,6},{-3,5},{-5,3},
        {-6,3},{-5,2},{-3,-2},{-3,-3},{-2,-3},{1,-5},{2,-6},{3,-7},{4,-8},
        {4,-7},{4,-6},{4,-5},{5,-4},{6,-4},{7,-4},{8,-4},{7,-3},{6,-2},
        {5,-1},{4,1},{4,2},{4,3},{4,4},{3,4},{2,4},{1,4},{-1,5},{-2,6},{-3,7},
        {-4,8},{-4,7},{-4,6},{-4,5},{-5,4},{-6,4},{-7,4},{-8,4},{-7,3},{-6,2},
        {-5,1},{-4,-1},{-4,-2},{-4,-3},{-4,-4},{-3,-4},{-2,-4},{-1,-4}
    };

    private static final int[][] NORTHWEST = {{-4,-4},{-4,-3},{-3,-4},{-4,-2},{-3,-3},{-2,-4},{-4,-1},{-3,-2},{-2,-3},{-1,-4}};
    private static final int[][] NORTH = {{4,-8},{3,-7},{4,-7},{2,-6},{3,-6},{4,-6},{1,-5},{2,-5},{3,-5},{4,-5}};
    private static final int[][] NORTHEAST = {{8,-4},{7,-4},{7,-3},{6,-4},{6,-3},{6,-2},{5,-4},{5,-3},{5,-2},{5,-1}};
    private static final int[][] SOUTHEAST = ChineseCheckersBoard.reflect(NORTHWEST);
    private static final int[][] SOUTH = ChineseCheckersBoard.reflect(NORTH);
    private static final int[][] SOUTHWEST = ChineseCheckersBoard.reflect(NORTHEAST);

    // Reflect points across the origin
    private static int[][] reflect(int[][] source){
        // Deep copy!
        int[][] result = source.clone();
        for(int i = 0; i < result.length; i++){
            result[i] = source[i].clone();
        }

        for(int[] pt : result){
            pt[0] = -pt[0];
            pt[1] = -pt[1];
        }
        return result;
    }


    private final HashMap<HexPoint, ChineseCheckersSpot> spots;

    public ChineseCheckersBoard(List<Player> players){
        spots = new HashMap<HexPoint, ChineseCheckersSpot>();

        assert(players.size() == 6);
        claim(players.get(0), NORTH);
        claim(players.get(1), NORTHEAST);
        claim(players.get(2), SOUTHEAST);
        claim(players.get(3), SOUTH);
        claim(players.get(4), SOUTHWEST);
        claim(players.get(5), NORTHWEST);

        // Fill in middle of board
        for (int[] coords : BOARD_COORDS){
            HexPoint location = new HexPoint(coords);
            if(!this.spots.containsKey(location)) this.spots.put(location, new ChineseCheckersSpot(location, null));
        }
    }

    public ChineseCheckersSpot getSpot(HexPoint location){
        return this.spots.get(location);
    }

//    public List<ChineseCheckersSpot> getSpotsOccupiedByPlayer(Player player){
//        List<ChineseCheckersSpot> pieces = new ArrayList<ChineseCheckersSpot>();
//        for(ChineseCheckersSpot spot : this.spots.values()){
//            if(spot.getOccupant() == player) pieces.add(spot);
//        }
//        return pieces;
//    }
//
//    public List<ChineseCheckersSpot> getAvailableMovesFromSpot(ChineseCheckersSpot spot){
//
//    }

    private void claim(Player player, int[][] corner){
        for(int[] coords : corner){
            HexPoint location = new HexPoint(coords);
            this.spots.put(location, new ChineseCheckersSpot(location, player));
        }
    }
}
