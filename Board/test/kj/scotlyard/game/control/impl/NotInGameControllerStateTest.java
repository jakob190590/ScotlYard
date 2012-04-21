package kj.scotlyard.game.control.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.Move;
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

	Set<StationVertex> initialStations;
	GameGraph gg;
	Game g;
	TheGameController c; // controller
	GameControllerState cs; // controller state
	Rules r;
	
	TheMoveProducer mp; // move producer
	
	@Before
	public void setUp() throws Exception {
		
		initialStations = new HashSet<>();
		
		r = new TheRules();
		g = new TheGame();
		c = new TheGameController(g, gg, initialStations, r);
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
		
		try {
			cs.start();
			fail("no exception");
		} catch (IllegalStateException e) { }
		g.getMoves().clear();		
		
		g.setMrX(null);		
		try {
			cs.start();
			fail("no exception");
		} catch (IllegalStateException e) { }
		g.setMrX(new MrXPlayer());		

		g.getDetectives().clear();
		for (int i = 1; i < 7; i++) {
			g.getDetectives().add(new DetectivePlayer());
			if (i < 3 || i > 5)
				try {
					cs.start();
					fail("no exception");
				} catch (IllegalStateException e) { }			
		}
		
		g.getDetectives().retainAll(g.getDetectives().subList(0, 4));
		
		// jetzt wird zum ersten mal ernsthaft gestartet:
		
		try {
			cs.start();
			fail("no exception");
		} catch (SecurityException e) {			
			// die muss kommen, weilcs den state von TheGameController nicht aendern darf
			e.printStackTrace();
		}
				
		assertEquals(g.getPlayers().size(), g.getMoves().size());
		int i = 0;
		for (Player p : g.getPlayers()) {
			Move m = g.getMoves().get(i++);
			assertEquals(p, m.getPlayer());	
			assertTrue(initialStations.contains(m.getStation()));
		}
		
		assertEquals(1, g.getCurrentRoundNumber());
		assertEquals(g.getMrX(), g.getCurrentPlayer());
		
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
