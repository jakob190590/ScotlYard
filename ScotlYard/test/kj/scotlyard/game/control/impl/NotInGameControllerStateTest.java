package kj.scotlyard.game.control.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.TheGame;
import kj.scotlyard.game.model.TheMoveProducer;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.rules.TheRules;

import org.junit.Before;
import org.junit.Test;

/**
 * Testet den Not in Game Zustand. Undo/Redo
 * kann leider hier ned getestet werden. Das
 * geht nur beim echten Controller.
 * @author jakob190590
 *
 */
public class NotInGameControllerStateTest {

	GameGraph gg;
	Game g;
	TheGameController c; // controller
	GameControllerState cs; // controller state
	Rules r;
	
	TheMoveProducer mp; // move producer
	
	@Before
	public void setUp() throws Exception {
		r = new TheRules();
		g = new TheGame();
		c = new TheGameController(g, gg, null, r);
		cs = new NotInGameControllerState(c);
		
		mp = TheMoveProducer.createInstance();
		
		g.getMoves().add(mp.createInitialMove(new DetectivePlayer(), new Station(gg)));
		g.getMoves().add(mp.createInitialMove(new DetectivePlayer(), new Station(gg)));
		
		g.setMrX(new MrXPlayer());
		
		g.getDetectives().add(new DetectivePlayer());
		g.getDetectives().add(new DetectivePlayer());
		g.getDetectives().add(new DetectivePlayer());
		g.getDetectives().add(new DetectivePlayer());
	}

	@Test
	public final void testGetStatus() {
		assertEquals(c.getStatus(), GameStatus.NOT_IN_GAME);
	}

	@Test
	public final void testNewGame() {
		cs.newGame();
		assertTrue(g.getMoves().isEmpty());
	}

	@Test
	public final void testClearPlayers() {
		c.clearPlayers();
		assertEquals(g.getMrX(), null);
		assertTrue(g.getDetectives().isEmpty());
		assertTrue(g.getPlayers().isEmpty());
	}

	@Test
	public final void testNewMrX() {
		MrXPlayer p = g.getMrX();
		cs.newMrX();
		assertFalse(g.getMrX().equals(p));
		assertFalse(g.getMrX().equals(null));
	}

	@Test
	public final void testNewDetective() {
		List<Player> ps = new ArrayList<>(g.getPlayers());
		c.newDetective();
		assertEquals(g.getPlayers().size(), ps.size() + 1);
		assertEquals(g.getPlayers().subList(0, ps.size()), ps);
		assertFalse(g.getDetectives().get(g.getDetectives().size() - 1).equals(null));
	}

	@Test
	public final void testRemoveDetective() {
		for (DetectivePlayer d : g.getDetectives()) {
			c.removeDetective(d);
			assertFalse(g.getDetectives().contains(d));
			assertFalse(g.getPlayers().contains(d));
		}
	}

	@Test
	public final void testShiftUpDetective() {
		
		for (DetectivePlayer d : g.getDetectives()) {
			int i = g.getDetectives().indexOf(d);
			while (i > 0) {
				i--;
				cs.shiftUpDetective(d);
				assertEquals(i, g.getDetectives().indexOf(d));
			}
			cs.shiftUpDetective(d); // nur um zu sehen, dass es keine exception gibt
			cs.shiftUpDetective(d);
		}
	}

	@Test
	public final void testShiftDownDetective() {
		
		for (DetectivePlayer d : g.getDetectives()) {
			int i = g.getDetectives().indexOf(d);
			while (i < (g.getDetectives().size() - 1)) {
				i++;
				cs.shiftDownDetective(d);
				assertEquals(i, g.getDetectives().indexOf(d));
			}
			cs.shiftDownDetective(d); // nur um zu sehen, dass es keine exception gibt
			cs.shiftDownDetective(d);
		}
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
