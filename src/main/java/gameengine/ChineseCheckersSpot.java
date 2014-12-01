package gameengine;

/**
 * Created by leo on 11/30/14.
 */
public class ChineseCheckersSpot {
    private final HexPoint location;
    private Player occupant;

    public HexPoint getLocation() { return location; }
    public Player getOccupant() {
        return occupant;
    }

    public boolean isEmpty(){
        return occupant == null;
    }

    public ChineseCheckersSpot(HexPoint location, Player player) {
        this.location = location;
        this.occupant = player;
    }

    public void moveOccupantTo(ChineseCheckersSpot dest){
        dest.occupant = this.getOccupant();
        this.occupant = null;
    }
}
