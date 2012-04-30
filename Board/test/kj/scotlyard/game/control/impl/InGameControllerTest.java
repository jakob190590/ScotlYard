package kj.scotlyard.game.control.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import kj.scotlyard.board.BoardGraphLoader;
import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.TheGame;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.util.MoveProducer;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.GameWinPolicy;
import kj.scotlyard.game.rules.MovePolicy;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.rules.TheGameWinPolicy;
import kj.scotlyard.game.rules.TheMovePolicy;
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
public class InGameControllerTest {

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
	DefaultGameController c; // controller
	
	@Before
	public void setUp() throws Exception {
		
		g = new TheGame();
		c = new DefaultGameController(g, gg, initialStations, r);
		
		g.setMrX(new MrXPlayer());
		
		g.getDetectives().add(new DetectivePlayer());
		g.getDetectives().add(new DetectivePlayer());
		g.getDetectives().add(new DetectivePlayer());
		g.getDetectives().add(new DetectivePlayer());
		
		
		c.start();
	}

	@Test
	public final void testGetStatus() {
		assertEquals(GameStatus.IN_GAME, c.getStatus());
	}

	@Test
	public final void testNewGame() {
		try {
			c.newGame();
			fail("no exception");
		} catch (IllegalStateException e) { }
	}

	@Test
	public final void testClearPlayers() {
		try {
			c.clearPlayers();
			fail("no exception");
		} catch (IllegalStateException e) { }
	}

	@Test
	public final void testNewMrX() {
		try {
			c.newMrX(); // not yet supported
			fail("no exception");
		} catch (IllegalStateException e) { }
	}

	@Test
	public final void testNewDetective() {
		try {
			c.newDetective(); // not yet supported
			fail("no exception");
		} catch (IllegalStateException e) { }
	}

	@Test
	public final void testRemoveDetective() {
		for (DetectivePlayer d : g.getDetectives()) {
			try {
				c.removeDetective(d); // not yet supported
				fail("no exception");
			} catch (IllegalStateException e) { }			
		}
	}

	@Test
	public final void testShiftUpDetective() {
		for (DetectivePlayer d : g.getDetectives()) {
			try {
				c.shiftUpDetective(d); // not yet supported
				fail("no exception");
			} catch (IllegalStateException e) { }			
		}
		/*
		for (DetectivePlayer d : g.getDetectives()) {
			int i = g.getDetectives().indexOf(d);
			while (i > 0) {
				i--;
				c.shiftUpDetective(d);
				assertEquals(i, g.getDetectives().indexOf(d));
			}
			c.shiftUpDetective(d); // nur um zu sehen, dass es keine exception gibt
			c.shiftUpDetective(d);
		}
		*/
	}

	@Test
	public final void testShiftDownDetective() {
		for (DetectivePlayer d : g.getDetectives()) {
			try {
				c.shiftDownDetective(d); // not yet supported
				fail("no exception");
			} catch (IllegalStateException e) { }			
		}
		/*
		for (DetectivePlayer d : g.getDetectives()) {
			int i = g.getDetectives().indexOf(d);
			while (i < (g.getDetectives().size() - 1)) {
				i++;
				c.shiftDownDetective(d);
				assertEquals(i, g.getDetectives().indexOf(d));
			}
			c.shiftDownDetective(d); // nur um zu sehen, dass es keine exception gibt
			c.shiftDownDetective(d);
		}
		*/
	}

	@Test
	public final void testStart() {
		
		try {
			c.start();
			fail("no exception");
		} catch (IllegalStateException e) { }
		
	}

	@Test
	public final void testAbort() {
		c.abort();
		assertEquals(GameWin.NO, c.getWin());
		assertEquals(GameStatus.NOT_IN_GAME, c.getStatus());
		
		c.getUndoManager().undo();
		assertEquals(GameWin.NO, c.getWin());
		assertEquals(GameStatus.IN_GAME, c.getStatus());
		
		c.getUndoManager().redo();
		assertEquals(GameWin.NO, c.getWin());
		assertEquals(GameStatus.NOT_IN_GAME, c.getStatus());
	}

	@Test
	public final void testMove() {
		MovePolicy movePolicy = new TheMovePolicy();
		GameWinPolicy winPolicy = new TheGameWinPolicy();		
		
		for (int i = 0; i < 100; i++) {
			Move m = mp.createNextBestSingleMove(g, gg);
			Set<Item> i1 = new HashSet<>(g.getItems(m.getPlayer()));
			
			GameState vorher = g.copy();
			
			c.move(m);
			
			GameState nacher = g.copy();
			
			assertEquals(m, g.getMoves().get(GameState.LAST_MOVE));

			// Schauen, ob Ticket richtig weitergegeben wurde
			Set<Item> i2 = new HashSet<>(g.getItems(m.getPlayer()));
			i1.removeAll(i2);
			assertEquals(1, i1.size());
			Ticket t = (Ticket) i1.iterator().next();
			assertTrue(movePolicy.isTicketValidForConnection(t, m.getConnection()));
			if (m.getPlayer() != g.getMrX()) {
				assertTrue(g.getItems(g.getMrX()).contains(t));
			}
			
			GameWin win = winPolicy.isGameWon(g, gg); 
			assertEquals(win, c.getWin());
			if (win != GameWin.NO) {
				assertEquals(GameStatus.NOT_IN_GAME, c.getStatus());
			}	
						
			// Undo ------------
			c.getUndoManager().undo();
			/*
			assertEquals(vorher.getCurrentPlayer(), g.getCurrentPlayer());
			assertEquals(vorher.getCurrentRoundNumber(), g.getCurrentRoundNumber());
			assertEquals(vorher.getMoves(), g.getMoves());
			assertEquals(vorher.getPlayers(), g.getPlayers());			
			for (Player p : g.getPlayers()) {
				assertEquals(vorher.getItems(p).size(), g.getItems(p).size());
			}
			*/
			assertEquals(GameStatus.IN_GAME, c.getStatus());
			assertEquals(GameWin.NO, c.getWin());
			
			assertEquals(vorher, g);

			// Redo ------------
			c.getUndoManager().redo();
			assertEquals(nacher, g);
			/*
			assertEquals(nacher.getCurrentPlayer(), g.getCurrentPlayer());
			assertEquals(nacher.getCurrentRoundNumber(), g.getCurrentRoundNumber());
			assertEquals(nacher.getMoves(), g.getMoves());
			assertEquals(nacher.getPlayers(), g.getPlayers());			
			for (Player p : g.getPlayers()) {				
				assertEquals(nacher.getItems(p), g.getItems(p));
			}
			*/			
			assertEquals(win, c.getWin());
			if (win != GameWin.NO) {
				System.out.println(win);
				assertEquals(GameStatus.NOT_IN_GAME, c.getStatus());
				break;
			}			
		}
	}

}
