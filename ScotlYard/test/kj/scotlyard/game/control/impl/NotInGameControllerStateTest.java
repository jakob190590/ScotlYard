package kj.scotlyard.game.control.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.TheGame;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.rules.TheRules;

import org.junit.Before;
import org.junit.Test;

public class NotInGameControllerStateTest {

	GameGraph gg;
	Game g;
	TheGameController c; // controller
	GameControllerState cs; // controller state
	Rules r;
	
	
	@Before
	public void setUp() throws Exception {
		r = new TheRules();
		g = new TheGame();
		c = new TheGameController(g, gg, null, r);
		cs = new NotInGameControllerState(c);
	}

	@Test
	public final void testGetStatus() {
		assertEquals(c.getStatus(), GameStatus.NOT_IN_GAME);
	}

	@Test
	public final void testNewGame() {
		fail("Not yet implemented");
		
		assertTrue(g.getMoves().isEmpty());
	}

	@Test
	public final void testClearPlayers() {
		fail("Not yet implemented");
		
		c.clearPlayers();
		assertEquals(g.getMrX(), null);
		assertTrue(g.getDetectives().isEmpty());
		assertTrue(g.getPlayers().isEmpty());
	}

	@Test
	public final void testNewMrX() {
		fail("Not yet implemented");
		
		Player p = g.getMrX();
		c.newMrX();
		assertFalse(g.getMrX().equals(p));
		assertFalse(g.getMrX().equals(null));
	}

	@Test
	public final void testNewDetective() {
		fail("Not yet implemented");
		
		List<Player> ps = new ArrayList<>(g.getPlayers());
		c.newDetective();
		assertEquals(g.getPlayers().size(), ps.size() + 1);
		assertEquals(g.getPlayers().subList(0, ps.size()), ps);
		assertFalse(g.getDetectives().get(g.getDetectives().size() - 1).equals(null));
	}

	@Test
	public final void testRemoveDetective() {
		fail("Not yet implemented");
		
		for (DetectivePlayer d : g.getDetectives()) {
			c.removeDetective(d);
			assertFalse(g.getDetectives().contains(d));
			assertFalse(g.getPlayers().contains(d));
		}
	}

	@Test
	public final void testShiftUpDetective() {
		fail("Not yet implemented");
	}

	@Test
	public final void testShiftDownDetective() {
		fail("Not yet implemented");
	}

	@Test
	public final void testStart() {
		fail("Not yet implemented");
	}

	@Test
	public final void testAbort() {
		try {
			c.abort();
			fail("no excption");
		} catch (IllegalStateException e) {			
		}
	}

	@Test
	public final void testMove() {
		try {
			c.move(new DefaultMove());
			fail("no excption");
		} catch (IllegalStateException e) {			
		}
	}

	@Test
	public final void testGetController() {
		assertEquals(cs.getController(), c);
	}

}
