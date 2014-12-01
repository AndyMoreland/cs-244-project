package gameengine;

/**
 * Created by leo on 11/30/14.
 */
public interface ChineseCheckersMove {
    public boolean canMoveAgain();
    public void apply(ChineseCheckersBoard board);
}
