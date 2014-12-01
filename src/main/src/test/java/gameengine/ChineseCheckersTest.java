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
    private Player steve;

    @org.junit.Before
    public void setUp() throws Exception {
        List<Player> players = new ArrayList<Player>();
        players.add(steve = new Player("Steve", 1001));

        this.game = new ChineseCheckersState(players);
    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @Test
    public void testBoardSetup(){
        Player occ = game.getSpot(new HexPoint(3, -5)).getOccupant();
        assertNotNull(occ);
        assertEquals(occ, steve);
        assertNull(game.getSpot(new HexPoint(0, 0)).getOccupant());
    }

    @Test
    public void testMoveValidity(){
        MovePiece moveEmptyHex = steveMove(0, 0, 1, 0);
        MovePiece moveOutOfBounds = steveMove(3, -7, 2, -7);
        MovePiece moveOntoOtherPiece = steveMove(3, -5, 4, -5);
        MovePiece goodMove = steveMove(3, -5, 3, -4);
        MovePiece goodJumpMove = steveMove(2, -6, 2, -4);
        MovePiece impostorMove = new MovePiece(666, 3, -5, 3, -4);

        assertFalse(moveEmptyHex.isValid(this.game));
        assertFalse(moveOutOfBounds.isValid(this.game));
        assertFalse(moveOntoOtherPiece.isValid(this.game));
        assertTrue(goodMove.isValid(this.game));
        assertTrue(goodJumpMove.isValid(this.game));
        assertFalse(impostorMove.isValid(this.game));
    }

    public void testMoveApplication(){
        ChineseCheckersSpot start = game.getSpot(new HexPoint(3, -5));
        ChineseCheckersSpot end = game.getSpot(new HexPoint(3, -4));
        MovePiece goodMove = steveMove(3, -5, 3, -4);

        assertTrue(goodMove.isValid(this.game));

        goodMove.apply(this.game);

        assertFalse(goodMove.isValid(this.game));
        assertNull(start.getOccupant());
        assertEquals(end.getOccupant(), steve);
    }

    private MovePiece steveMove(int a, int b, int c, int d){ return new MovePiece(1001, a, b, c, d); }
}
