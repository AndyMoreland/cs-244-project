package gameengine;

/**
 * Created by leo on 11/30/14.
 */
public enum HexDirection {
    NORTHEAST(1, -1),
    EAST(1, 0),
    SOUTHEAST(0, 1),
    SOUTHWEST(-1, 1),
    WEST(-1, 0),
    NORTHWEST(0, -1);

    private final HexPoint offset;
    public HexPoint getOffset() { return this.offset; }

    private HexDirection(int q, int r){
        this.offset = new HexPoint(q, r);
    }

    public static HexDirection lookup(HexPoint offset){
        for (HexDirection dir : HexDirection.values()){
            if(dir.offset.equals(offset)) return dir;
        }
        return null;
    }
}
