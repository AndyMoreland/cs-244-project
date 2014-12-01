package gameengine;

import gameengine.operations.MovePiece;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by leo on 11/30/14.
 */
public class ChineseCheckersTest {
    private ChineseCheckersState game;

    @org.junit.Before
    public void setUp() throws Exception {
        List<Player> players = new ArrayList<Player>();
        players.add(new Player("Steve", 1));

        this.game = new ChineseCheckersState(players);
    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @Test
    public void testMoveValidity(){
        MovePiece moveEmptyHex = new MovePiece(0, 0, 1, 0);
        MovePiece moveOutOfBounds = new MovePiece(3, -7, 2, -7);
        MovePiece moveOntoOtherPiece = new MovePiece(3, -5, 4, -5);
        MovePiece goodMove = new MovePiece(3, -5, 3, -4);
        MovePiece goodJumpMove = new MovePiece(2, -6, 2, -4);

        assertFalse(moveEmptyHex.isValid(this.game));
        assertFalse(moveOutOfBounds.isValid(this.game));
        assertFalse(moveOntoOtherPiece.isValid(this.game));
        assertTrue(goodMove.isValid(this.game));
        assertTrue(goodJumpMove.isValid(this.game));
    }
}
