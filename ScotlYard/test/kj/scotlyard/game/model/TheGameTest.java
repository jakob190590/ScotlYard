package kj.scotlyard.game.model;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.model.GameState.MoveAccessMode;
import kj.scotlyard.game.model.items.BlackTicket;
import kj.scotlyard.game.model.items.BusTicket;
import kj.scotlyard.game.model.items.DoubleMoveCard;
import kj.scotlyard.game.model.items.Item;
import kj.scotlyard.game.model.items.TaxiTicket;
import kj.scotlyard.game.model.items.UndergroundTicket;

import org.junit.Before;
import org.junit.Test;

public class TheGameTest {

	Game g;
	MrXPlayer mrX;
	DetectivePlayer d1, d2, d3, d4;
	Move[] ms = new Move[20];
	Move m1, m2;
	
	@Before
	public void setUp() throws Exception {
		g = new TheGame();
		
		mrX = new MrXPlayer();
		d1 = new DetectivePlayer();
		d2 = new DetectivePlayer();
		d3 = new DetectivePlayer();
		d4 = new DetectivePlayer();
		
		g.setMrX(mrX);
		g.getDetectives().add(d1);
		g.getDetectives().add(d2);
		g.getDetectives().add(d3);
		g.getDetectives().add(d4);
		
		for (int i = 0; i < 20; i++) {
			int round = i % g.getPlayers().size();
			ms[i] = new TheMove(g.getPlayers().get(round), new TaxiConnection(), new StationVertex(), new TaxiTicket());
		}
		/*
		ms[0] = new TheMove(mrX, new StationVertex());
		ms[1] = new TheMove(d1, new StationVertex());
		ms[2] = new TheMove(d2, new StationVertex());
		ms[3] = new TheMove(d3, new StationVertex());
		ms[4] = new TheMove(d4, new StationVertex()); */
		
		m1 = new TheMove(mrX, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		m1.setMoveIndex(0);
		m1.setMoveNumber(1);
		m1.setRoundNumber(1);
		m2 = new TheMove(mrX, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		m2.setMoveIndex(1);
		m2.setMoveNumber(2);
		m2.setRoundNumber(1);
		ms[5] = new TheMove(mrX, new DoubleMoveCard(), m1, m2);	/*			
		ms[6] = new TheMove(d1, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		ms[7] = new TheMove(d2, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		ms[8] = new TheMove(d3, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		ms[9] = new TheMove(d4, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		
		ms[10] = new TheMove(mrX, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		ms[11] = new TheMove(d1, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		ms[12] = new TheMove(d2, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		ms[13] = new TheMove(d3, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		ms[14] = new TheMove(d4, new TaxiConnection(), new StationVertex(), new TaxiTicket()); */
		
		m1 = new TheMove(mrX, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		m1.setMoveIndex(0);
		m1.setMoveNumber(4);
		m1.setRoundNumber(3);
		m2 = new TheMove(mrX, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		m2.setMoveIndex(1);
		m2.setMoveNumber(5);
		m2.setRoundNumber(3);
		ms[15] = new TheMove(mrX, new DoubleMoveCard(), m1, m2); /*
		ms[16] = new TheMove(d1, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		ms[17] = new TheMove(d2, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		ms[18] = new TheMove(d3, new TaxiConnection(), new StationVertex(), new TaxiTicket());
		ms[19] = new TheMove(d4, new TaxiConnection(), new StationVertex(), new TaxiTicket()); */
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 5; j++) {
				ms[i * 5 + j].setRoundNumber(i);
				ms[i * 5 + j].setMoveNumber(i);
				ms[i * 5 + j].setMoveIndex(Move.NO_MOVE_INDEX);
			}
		}
		
		ms[5].setMoveNumber(Move.NO_MOVE_NUMBER);
		ms[5].setMoveIndex(Move.NO_MOVE_INDEX);
		ms[15].setMoveNumber(Move.NO_MOVE_NUMBER);
		ms[15].setMoveIndex(Move.NO_MOVE_INDEX);
		
		
	}

	@Test
	public final void testGetMrX() {
		g.setMrX(null);
		assertEquals(null, g.getMrX());
		g.setMrX(mrX);
		assertEquals(mrX, g.getMrX());
	}

	@Test
	public final void testGetDetectives() {
		try {
			g.getDetectives().set(2, d3);
			fail("set works");
		} catch (Exception e) { }
		
		g.getDetectives().add(d1);
		g.getDetectives().add(0, d4);
		g.getDetectives().remove(d4);
		g.getDetectives().remove(4);
		
		assertEquals(4, g.getDetectives().size());
		
		assertTrue(g.getDetectives().contains(d1));
		assertTrue(g.getDetectives().contains(d4));
		assertTrue(!g.getDetectives().contains(mrX));
		
		assertEquals(0, g.getDetectives().indexOf(d1));
		assertEquals(3, g.getDetectives().indexOf(d4));
		
		assertEquals(d1, g.getDetectives().get(0));
		assertEquals(d3, g.getDetectives().get(2));
		assertEquals(d4, g.getDetectives().get(3));
	}

	@Test
	public final void testGetPlayers() {
		try {
			g.getPlayers().set(2, d3);
			fail("set works");
		} catch (Exception e) { }
		
		try {
			g.getPlayers().clear();
			fail("clear works");
		} catch (Exception e) { }
		
		try {
			g.getPlayers().add(d3);
			fail("add works");
		} catch (Exception e) { }
		
		try {
			g.getPlayers().remove(mrX);
			fail("rm works");
		} catch (Exception e) { }
				
		assertEquals(5, g.getPlayers().size());
		
		assertTrue(g.getPlayers().contains(d1));
		assertTrue(g.getPlayers().contains(d4));
		assertTrue(g.getPlayers().contains(mrX));
		
		assertEquals(0, g.getPlayers().indexOf(mrX));
		assertEquals(1, g.getPlayers().indexOf(d1));
		assertEquals(4, g.getPlayers().indexOf(d4));
		
		assertEquals(mrX, g.getPlayers().get(0));
		assertEquals(d1, g.getPlayers().get(1));
		assertEquals(d4, g.getPlayers().get(4));
	}

	@Test
	public final void testGetItems() {
		assertEquals(null, g.getItems(mrX));
		assertEquals(null, g.getItems(d1));
		
		Set<Item> s = new HashSet<Item>();
		g.setItems(mrX, s);		
		assertEquals(0, g.getItems(mrX).size());
		
		s.add(new DoubleMoveCard());
		s.add(new BusTicket());
		s.add(new UndergroundTicket());
		g.setItems(mrX, s);
		assertEquals(3, g.getItems(mrX).size());
		
		s.add(new BlackTicket());
		g.setItems(d1, s);
		assertEquals(3, g.getItems(mrX).size());
		assertEquals(4, g.getItems(d1).size());
		
		
		// ItemList testen
		s = g.getItems(d1);
		s.add(new BusTicket());
		assertEquals(5, s.size());
		
		Iterator<Item> it = s.iterator();
		int i = 0;
		while (it.hasNext()) {
			it.next();
			if (i == 2) {
				it.remove();
			}
			i++;
		}
		assertEquals(4, s.size());
		
		s.clear();
		assertEquals(0, s.size());
	}

	@Test
	public final void testGetMoves() {
		for (int i = 0; i < 5; i++) {
			g.getMoves().add(ms[i]);
		}
		
		try {
			g.getMoves().set(3, ms[7]);
			fail("set works");
		} catch (Exception e) { }
		
		try {
			g.getMoves().remove(ms[2]);
			fail("rm works");
		} catch (Exception e) { }
		
		try {
			g.getMoves().remove(0);
			fail("rm works");
		} catch (Exception e) { }
				
		try {
			g.getMoves().remove(g.getMoves().size() - 1);
			fail("rm works");
		} catch (Exception e) { }
		
		g.getMoves().remove(GameState.LAST_MOVE);
		g.getMoves().remove(GameState.LAST_MOVE);
		
		assertEquals(3, g.getMoves().size());
		
		assertEquals(0, g.getMoves().indexOf(ms[0]));
		assertEquals(1, g.getMoves().indexOf(ms[1]));
		assertEquals(2, g.getMoves().indexOf(ms[2]));
		
		g.getMoves().clear();
		
		assertEquals(0, g.getMoves().size());
	}

	@Test
	public final void testGetMove() {
		g.getMoves().clear();
		for (Move m : ms) {
			g.getMoves().add(m);
		}
		
		
		// ungueltige Move accesses von mrX
		// by move number
		try {
			g.getMove(mrX, -7, MoveAccessMode.MOVE_NUMBER);
			fail("no exception");
		} catch (Exception e) { }
		
		try {
			g.getMove(mrX, -8, MoveAccessMode.MOVE_NUMBER);
			fail("no exception");
		} catch (Exception e) { }
		
		try {
			g.getMove(mrX, 6, MoveAccessMode.MOVE_NUMBER);
			fail("no exception");
		} catch (Exception e) { }
		
		try {
			g.getMove(mrX, 7, MoveAccessMode.MOVE_NUMBER);
			fail("no exception");
		} catch (Exception e) { }
		
		// by round number
		try {
			g.getMove(mrX, -5, MoveAccessMode.ROUND_NUMBER);
			fail("no exception");
		} catch (Exception e) { }
		
		try {
			g.getMove(mrX, -6, MoveAccessMode.ROUND_NUMBER);
			fail("no exception");
		} catch (Exception e) { }
		
		try {
			g.getMove(mrX, 4, MoveAccessMode.ROUND_NUMBER);
			fail("no exception");
		} catch (Exception e) { }
		
		try {
			g.getMove(mrX, 5, MoveAccessMode.ROUND_NUMBER);
			fail("no exception");
		} catch (Exception e) { }
		
		// ungueltige Move accesses von detectives
		for (DetectivePlayer d : g.getDetectives()) {
			// by move number
			try {
				g.getMove(d, -5, MoveAccessMode.MOVE_NUMBER);
				fail("no exception");
			} catch (Exception e) { }
			
			try {
				g.getMove(d, -6, MoveAccessMode.MOVE_NUMBER);
				fail("no exception");
			} catch (Exception e) { }
			
			try {
				g.getMove(d, 4, MoveAccessMode.MOVE_NUMBER);
				fail("no exception");
			} catch (Exception e) { }
			
			try {
				g.getMove(d, 5, MoveAccessMode.MOVE_NUMBER);
				fail("no exception");
			} catch (Exception e) { }
						
			// by round number
			try {
				g.getMove(d, -5, MoveAccessMode.ROUND_NUMBER);
				fail("no exception");
			} catch (Exception e) { }
			
			try {
				g.getMove(d, -6, MoveAccessMode.ROUND_NUMBER);
				fail("no exception");
			} catch (Exception e) { }
			
			try {
				g.getMove(d, 4, MoveAccessMode.ROUND_NUMBER);
				fail("no exception");
			} catch (Exception e) { }
			
			try {
				g.getMove(d, 5, MoveAccessMode.ROUND_NUMBER);
				fail("no exception");
			} catch (Exception e) { }
		}
		
		
		
		
		
		// gueltige Move accesses von mrX
		// by move number
		assertEquals(ms[0], g.getMove(mrX, -6, MoveAccessMode.MOVE_NUMBER));
		assertEquals(ms[5].getMoves().get(0), g.getMove(mrX, -5, MoveAccessMode.MOVE_NUMBER));
		assertEquals(ms[15].getMoves().get(0), g.getMove(mrX, -2, MoveAccessMode.MOVE_NUMBER));
		assertEquals(ms[15].getMoves().get(1), g.getMove(mrX, -1, MoveAccessMode.MOVE_NUMBER));
		assertEquals(ms[0], g.getMove(mrX, 0, MoveAccessMode.MOVE_NUMBER));
		assertEquals(ms[5].getMoves().get(0), g.getMove(mrX, 1, MoveAccessMode.MOVE_NUMBER));
		assertEquals(ms[15].getMoves().get(0), g.getMove(mrX, 4, MoveAccessMode.MOVE_NUMBER));
		assertEquals(ms[15].getMoves().get(1), g.getMove(mrX, 5, MoveAccessMode.MOVE_NUMBER));
		
		// by round number
		assertEquals(ms[0], g.getMove(mrX, -4, MoveAccessMode.ROUND_NUMBER));
		assertEquals(ms[5], g.getMove(mrX, -3, MoveAccessMode.ROUND_NUMBER));
		assertEquals(ms[10], g.getMove(mrX, -2, MoveAccessMode.ROUND_NUMBER));
		assertEquals(ms[15], g.getMove(mrX, -1, MoveAccessMode.ROUND_NUMBER));
		assertEquals(ms[0], g.getMove(mrX, 0, MoveAccessMode.ROUND_NUMBER));
		assertEquals(ms[5], g.getMove(mrX, 1, MoveAccessMode.ROUND_NUMBER));
		assertEquals(ms[10], g.getMove(mrX, 2, MoveAccessMode.ROUND_NUMBER));
		assertEquals(ms[15], g.getMove(mrX, 3, MoveAccessMode.ROUND_NUMBER));
		
		// gueltige Move accesses von detectives
		int i = 1;
		for (DetectivePlayer d : g.getDetectives()) {
			// by move number		
			assertEquals(ms[i], g.getMove(d, -4, MoveAccessMode.MOVE_NUMBER));
			assertEquals(ms[i + 5], g.getMove(d, -3, MoveAccessMode.MOVE_NUMBER));
			assertEquals(ms[i + 10], g.getMove(d, -2, MoveAccessMode.MOVE_NUMBER));
			assertEquals(ms[i + 15], g.getMove(d, -1, MoveAccessMode.MOVE_NUMBER));
			assertEquals(ms[i + 0], g.getMove(d, 0, MoveAccessMode.MOVE_NUMBER));
			assertEquals(ms[i + 5], g.getMove(d, 1, MoveAccessMode.MOVE_NUMBER));
			assertEquals(ms[i + 10], g.getMove(d, 2, MoveAccessMode.MOVE_NUMBER));
			assertEquals(ms[i + 15], g.getMove(d, 3, MoveAccessMode.MOVE_NUMBER));
			
			// by round number
			assertEquals(ms[i], g.getMove(d, -4, MoveAccessMode.ROUND_NUMBER));
			assertEquals(ms[i + 5], g.getMove(d, -3, MoveAccessMode.ROUND_NUMBER));
			assertEquals(ms[i + 10], g.getMove(d, -2, MoveAccessMode.ROUND_NUMBER));
			assertEquals(ms[i + 15], g.getMove(d, -1, MoveAccessMode.ROUND_NUMBER));
			assertEquals(ms[i + 0], g.getMove(d, 0, MoveAccessMode.ROUND_NUMBER));
			assertEquals(ms[i + 5], g.getMove(d, 1, MoveAccessMode.ROUND_NUMBER));
			assertEquals(ms[i + 10], g.getMove(d, 2, MoveAccessMode.ROUND_NUMBER));
			assertEquals(ms[i + 15], g.getMove(d, 3, MoveAccessMode.ROUND_NUMBER));
			
			i++;
		}
		
		
		
		g.getMoves().clear();		
	}

	@Test
	public final void testGetLastMovePlayer() {
		try {
			g.getLastMove(d1);
			fail("no excpetion");
		} catch (Exception e) { }
		
		try {
			g.getLastMove(mrX);
			fail("no excpetion");
		} catch (Exception e) { }
		
		for (int i = 0; i < 8; i++) {
			g.getMoves().add(ms[i]);
		}		
		
		assertEquals(ms[7], g.getLastMove(d2));		
		g.getMoves().remove(GameState.LAST_MOVE);
		assertEquals(ms[2], g.getLastMove(d2));
		
		assertEquals(ms[6], g.getLastMove(d1));
		assertEquals(ms[5], g.getLastMove(mrX));
		assertEquals(ms[4], g.getLastMove(d4));
		assertEquals(ms[3], g.getLastMove(d3));
		
		g.getMoves().clear();		
	}

	@Test
	public final void testGetLastMove() {
		assertEquals(null, g.getLastMove());
		
		for (int i = 0; i < 5; i++) {
			g.getMoves().add(ms[i]);
		}		
		
		g.getMoves().remove(GameState.LAST_MOVE);
		g.getMoves().remove(GameState.LAST_MOVE);		
		assertEquals(ms[2], g.getLastMove());
		
		g.getMoves().remove(GameState.LAST_MOVE);		
		assertEquals(ms[1], g.getLastMove());
		
		g.getMoves().clear();		
		assertEquals(null, g.getLastMove());
	}

	@Test
	public final void testSetGetCurrentRoundNumber() {
		assertEquals(GameState.INITIAL_ROUND_NUMBER, g.getCurrentRoundNumber());
		g.setCurrentRoundNumber(5);
		assertEquals(5, g.getCurrentRoundNumber());
		g.setCurrentRoundNumber(1);
		assertEquals(1, g.getCurrentRoundNumber());
		g.setCurrentRoundNumber(GameState.INITIAL_ROUND_NUMBER);
		assertEquals(GameState.INITIAL_ROUND_NUMBER, g.getCurrentRoundNumber());
	}

	@Test
	public final void testSetMrX() {
		assertEquals(mrX, g.getMrX());
		g.setMrX(null);
		assertEquals(null, g.getMrX());
		g.setMrX(mrX);
		assertEquals(mrX, g.getMrX());
	}

	@Test
	public final void testSetItems() {
		assertEquals(null, g.getItems(d4));
		g.setItems(d4, new HashSet<Item>());
		assertTrue(g.getItems(d4) != null);
		g.setItems(d4, null);
		assertEquals(null, g.getItems(d4));
	}

	@Test
	public final void testSetGetCurrentPlayer() {
		assertEquals(null, g.getCurrentPlayer());
		g.setCurrentPlayer(mrX);
		assertEquals(mrX, g.getCurrentPlayer());
		g.setCurrentPlayer(d1);
		assertEquals(d1, g.getCurrentPlayer());
		g.setCurrentPlayer(null);
		assertEquals(null, g.getCurrentPlayer());
	}

}
