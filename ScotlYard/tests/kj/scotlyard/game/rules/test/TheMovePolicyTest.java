package kj.scotlyard.game.rules.test;

import static org.junit.Assert.*;
import kj.scotlyard.game.Game;
import kj.scotlyard.game.TheGame;
import kj.scotlyard.game.rules.MovePolicy;
import kj.scotlyard.game.rules.TheMovePolicy;

import org.junit.Before;
import org.junit.Test;

public class TheMovePolicyTest {
	
	MovePolicy movePolicy;

	@Before
	public void setUp() throws Exception {
		Game game = new TheGame();
		movePolicy = new TheMovePolicy(game);
	}

	@Test
	public void testCheckValidness() {
		fail("Not yet implemented");
	}

	@Test
	public void testToString() {
		assertEquals("The official Scotland Yard rules MovePolicy implementation!", movePolicy.toString());
	}

}
