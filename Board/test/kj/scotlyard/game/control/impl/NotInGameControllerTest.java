package kj.scotlyard.game.control.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kj.scotlyard.board.BoardGraphLoader;
import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.DefaultGame;
import kj.scotlyard.game.util.MoveProducer;
import kj.scotlyard.game.rules.GameWin;
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
public class NotInGameControllerTest {

	final BoardGraphLoader loader = new BoardGraphLoader();
	final Set<StationVertex> initialStations;
	final GameGraph gg;
	{
		try {
			loader.load("graph-description", "initial-stations");
		} catch (IOException e) {
			e.printStackTrace();
		}				
		gg = loader.getGameGraph();
		initialStations = loader.getInitialStations();
	}
	
	final MoveProducer mp = MoveProducer.createInstance(); // move producer
	
	final Rules r = new TheRules();
	
	Game g;
	TheGameController c; // controller
	
	@Before
	public void setUp() throws Exception {
		
		g = new DefaultGame();
		c = new TheGameController(g, gg, initialStations, r);
		
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
		List<Move> ms = new ArrayList<>(g.getMoves());
		
		c.newGame();
		assertTrue(g.getMoves().isEmpty());
		
		c.getUndoManager().undo();
		assertEquals(ms, g.getMoves());
		
		c.getUndoManager().redo();
		assertTrue(g.getMoves().isEmpty());
	}

	@Test
	public final void testClearPlayers() {
		List<Player> ps = new ArrayList<>(g.getPlayers());
		
		c.clearPlayers();
		assertEquals(g.getMrX(), null);
		assertTrue(g.getDetectives().isEmpty());
		assertTrue(g.getPlayers().isEmpty());
		
		c.getUndoManager().undo();
		assertEquals(ps, g.getPlayers());
		
		c.getUndoManager().redo();
		assertEquals(g.getMrX(), null);
		assertTrue(g.getDetectives().isEmpty());
		assertTrue(g.getPlayers().isEmpty());
	}

	@Test
	public final void testNewMrX() {
		MrXPlayer p1 = g.getMrX();
		c.newMrX();
		MrXPlayer p2 = g.getMrX();
		assertFalse(p2.equals(p1));
		assertFalse(p2.equals(null));
		
		c.getUndoManager().undo();
		assertEquals(p1, g.getMrX());
		
		c.getUndoManager().redo();
		assertEquals(p2, g.getMrX());
	}

	@Test
	public final void testNewDetective() {
		List<Player> ps1 = new ArrayList<>(g.getPlayers());
		c.newDetective();
		List<Player> ps2 = new ArrayList<>(g.getPlayers());
		assertEquals(ps2.size(), ps1.size() + 1);
		assertEquals(ps2.subList(0, ps1.size()), ps1);
		assertFalse(g.getDetectives().get(g.getDetectives().size() - 1).equals(null));
		
		c.getUndoManager().undo();
		assertEquals(ps1, g.getPlayers());
		
		c.getUndoManager().redo();
		assertEquals(ps2, g.getPlayers());
	}

	@Test
	public final void testRemoveDetective() {
		for (DetectivePlayer d : g.getDetectives()) {
			int i = g.getPlayers().indexOf(d);
			c.removeDetective(d);
			assertFalse(g.getDetectives().contains(d));
			assertFalse(g.getPlayers().contains(d));
			
			c.getUndoManager().undo();
			assertEquals(d, g.getPlayers().get(i));
			
			c.getUndoManager().redo();
			assertFalse(g.getDetectives().contains(d));
		}
	}

	@Test
	public final void testShiftUpDetective() {
		
		for (DetectivePlayer d : g.getDetectives()) {
			int i = g.getDetectives().indexOf(d);
			while (i > 0) {
				GameState gs1 = g.copy();
				
				i--;
				c.shiftUpDetective(d);
				assertEquals(i, g.getDetectives().indexOf(d));
				
				GameState gs2 = g.copy();
				if (!gs1.getDetectives().equals(gs2.getDetectives())) {
					
					c.getUndoManager().undo();
					assertEquals(gs1, g);
					
					c.getUndoManager().redo();
					assertEquals(gs2, g);
				}
				
			}
			c.shiftUpDetective(d); // nur um zu sehen, dass es keine exception gibt
			c.shiftUpDetective(d);			
			
		}
	}

	@Test
	public final void testShiftDownDetective() {
		
		for (DetectivePlayer d : g.getDetectives()) {
			int i = g.getDetectives().indexOf(d);
			while (i < (g.getDetectives().size() - 1)) {
				GameState gs1 = g.copy();
				
				i++;
				c.shiftDownDetective(d);
				assertEquals(i, g.getDetectives().indexOf(d));
				
				GameState gs2 = g.copy();
				if (!gs1.getDetectives().equals(gs2.getDetectives())) {
					
					c.getUndoManager().undo();
					assertEquals(gs1, g);
					
					c.getUndoManager().redo();
					assertEquals(gs2, g);
				}
				
			}
			c.shiftDownDetective(d); // nur um zu sehen, dass es keine exception gibt
			c.shiftDownDetective(d);
		}
	}

	@Test
	public final void testStart() {
		
		try {
			c.start();
			fail("no exception");
		} catch (IllegalStateException e) { }
		g.getMoves().clear();		
		
		g.setMrX(null);		
		try {
			c.start();
			fail("no exception");
		} catch (IllegalStateException e) { }
		g.setMrX(new MrXPlayer());		

		g.getDetectives().clear();
		for (int i = 1; i < 7; i++) {
			g.getDetectives().add(new DetectivePlayer());
			if (i < 3 || i > 5)
				try {
					c.start();
					fail("no exception");
				} catch (IllegalStateException e) { }			
		}
		
		g.getDetectives().retainAll(g.getDetectives().subList(0, 4));
		
		// jetzt wird zum ersten mal ernsthaft gestartet:
		assertEquals(GameStatus.NOT_IN_GAME, c.getStatus());
		
		c.start();
		
		assertEquals(GameWin.NO, c.getWin());
		assertEquals(GameStatus.IN_GAME, c.getStatus());
				
		assertEquals(g.getPlayers().size(), g.getMoves().size());
		int i = 0;
		for (Player p : g.getPlayers()) {
			Move m = g.getMoves().get(i++);
			assertEquals(p, m.getPlayer());	
			assertTrue(initialStations.contains(m.getStation()));
		}
		
		assertEquals(1, g.getCurrentRoundNumber());
		assertEquals(g.getMrX(), g.getCurrentPlayer());
		
		
		assertEquals(false, c.getUndoManager().canUndo());
		
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

}
