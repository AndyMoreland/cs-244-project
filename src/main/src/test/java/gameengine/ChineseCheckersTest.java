package gameengine;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    }
}
