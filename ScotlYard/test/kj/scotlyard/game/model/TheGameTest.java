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
	
	class TestStateListener implements StateListener {

		GameState gs;
		
		Player oldP, newP;
		
		int oldR, newR;
		
		@Override
		public void currentPlayerChanged(GameState gameState, Player oldPlayer,
				Player newPlayer) {
			gs = gameState;
			oldP = oldPlayer;
			newP = newPlayer;
			
		}

		@Override
		public void currentRoundChanged(GameState gameState,
				int oldRoundNumber, int newRoundNumber) {
			gs = gameState;
			oldR = oldRoundNumber;
			newR = newRoundNumber;
		}
		
	}
	
	class TestPlayerListener implements PlayerListener {

		GameState gs;
		
		int i;
		
		DetectivePlayer d;
		
		MrXPlayer oldX, newX;
		
		@Override
		public void detectiveAdded(GameState gameState,
				DetectivePlayer detective, int atIndex) {
			gs = gameState;
			d = detective;
			i = atIndex;			
		}

		@Override
		public void detectiveRemoved(GameState gameState,
				DetectivePlayer detective, int atIndex) {
			gs = gameState;
			d = detective;
			i = atIndex;
		}

		@Override
		public void mrXSet(GameState gameState, MrXPlayer oldMrX,
				MrXPlayer newMrX) {
			gs = gameState;
			oldX = oldMrX;
			newX = newMrX;
		}
		
	}
	
	class TestItemListener implements ItemListener {

		GameState gs;
		
		Item i;
		
		Player p;
		
		Set<Item> oldI, newI;
		
		@Override
		public void itemAdded(GameState gameState, Player player, Item item) {
			gs = gameState;
			p = player;
			i = item;
		}

		@Override
		public void itemRemoved(GameState gameState, Player player, Item item) {
			gs = gameState;
			p = player;
			i = item;
		}

		@Override
		public void itemSetChanged(GameState gameState, Player player,
				Set<Item> oldItems, Set<Item> newItems) {
			gs = gameState;
			p = player;
			oldI = oldItems;
			newI = newItems;
		}
		
	}
	
	class TestMoveListener implements MoveListener {

		GameState gs;
		
		Move m;
		
		@Override
		public void moveDone(GameState gameState, Move move) {
			gs = gameState;
			m = move;			
		}

		@Override
		public void moveUndone(GameState gameState, Move move) {
			gs = gameState;
			m = move;
		}

		@Override
		public void movesCleard(GameState gameState) {
			gs = gameState;
		}
		
	}

	Game g;
	TheMoveProducer prod = TheMoveProducer.createInstance();
	MrXPlayer mrX;
	DetectivePlayer d1, d2, d3, d4;
	Move[] ms = new Move[100];
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
		
		int j = 0;
		for (int i = 0; i < 20; i++) {
			for (Player p : g.getPlayers()) {
				ms[j] = prod.createSingleMove(p, i, i,
						new StationVertex(), new TaxiConnection(), new TaxiTicket());
				j++;
			}
		}

		prod.addSubMove(new StationVertex(), new TaxiConnection(),
				new TaxiTicket());
		prod.addSubMove(new StationVertex(), new TaxiConnection(),
				new TaxiTicket());
		ms[5] = prod.createMultiMove(mrX, 1, 1, new DoubleMoveCard());

		prod.addSubMove(new StationVertex(), new TaxiConnection(),
				new TaxiTicket());
		prod.addSubMove(new StationVertex(), new TaxiConnection(),
				new TaxiTicket());
		ms[15] = prod.createMultiMove(mrX, 3, 4, new DoubleMoveCard());

		int n = 0;
		for (Move m : ms) {

			if (m.getPlayer() == mrX) {
				if (m.getMoveNumber() >= 0)
					m.setMoveNumber(n++);
				else
					n += m.getMoves().size();
			}

			g.getMoves().add(m);
		}

//		for (Move m : g.getMoves()) {
//			if (m.getPlayer() == mrX) {
//				System.out.println("round number: " + m.getRoundNumber()
//						+ "    move number: " + m.getMoveNumber());
//				for (Move o : m.getMoves()) {
//					System.out.println("round number: " + o.getRoundNumber()
//							+ "    move number: " + o.getMoveNumber());
//				}
//			}
//		}		
		
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
		
		try {
			g.getDetectives().add(d3);
			fail("duplicates are allowed");
		} catch (Exception e) { }
		
		DetectivePlayer dx = new DetectivePlayer();
		DetectivePlayer dy = new DetectivePlayer();
		g.getDetectives().add(dx);
		g.getDetectives().add(0, dy);
		g.getDetectives().remove(dy);
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
		
		// doppelt adden darf nicht moeglich sein
		Item item = new DoubleMoveCard();
		s = g.getItems(d1);
		assertEquals(true, s.add(item));
		assertEquals(false, s.add(item));
	}

	@Test
	public final void testGetMoves() {
		
		g.getMoves().clear();
		
		for (int i = 0; i < 5; i++) {
			g.getMoves().add(ms[i]);
		}
		
		// doppelt adden duerfte nicht gehen:
		for (int i = 0; i < 5; i++) {
			try {
				g.getMoves().add(ms[i]);
				fail("duplicates are allowed");
			} catch (Exception e) { }
		}
		
		// zugriff von hinten
		assertEquals(ms[4], g.getMoves().get(-1));
		assertEquals(ms[3], g.getMoves().get(-2));
		assertEquals(ms[1], g.getMoves().get(-4));
		assertEquals(ms[0], g.getMoves().get(-5));
		try {
			g.getMoves().get(-6);
			fail("no index out of bound exc??");
		} catch (Exception e) { }
		
		
		
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
		for (int i = 0; i < 20; i++) {			
			g.getMoves().add(ms[i]);
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
		
		assertEquals(null, g.getLastMove(new DetectivePlayer()));
		
		g.getMoves().clear();
		
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
	
	@Test
	public final void testStateListener() {
		TestStateListener l = new TestStateListener();
		g.addStateListener(l);
		
		g.setCurrentPlayer(null);
		assertEquals(null, l.newP);
		assertEquals(g, l.gs);
		g.setCurrentPlayer(d3);
		assertEquals(null, l.oldP);
		assertEquals(d3, l.newP);
		g.setCurrentPlayer(null);
		assertEquals(d3, l.oldP);
		assertEquals(null, l.newP);
		
		
		g.setCurrentRoundNumber(2);
		assertEquals(2, l.newR);
		assertEquals(g, l.gs);
		g.setCurrentRoundNumber(1);
		assertEquals(2, l.oldR);
		assertEquals(1, l.newR);
		g.setCurrentRoundNumber(0);
		assertEquals(1, l.oldR);
		assertEquals(0, l.newR);
	}

	@Test
	public final void testPlayerListener() {
		TestPlayerListener l = new TestPlayerListener();
		g.addPlayerListener(l);
		
		g.setMrX(null);
		assertEquals(null, l.newX);
		assertEquals(g, l.gs);
		g.setMrX(mrX);
		assertEquals(null, l.oldX);
		assertEquals(mrX, l.newX);
		g.setMrX(null);
		assertEquals(mrX, l.oldX);
		assertEquals(null, l.newX);
		
		DetectivePlayer dx = new DetectivePlayer();
		DetectivePlayer dy = new DetectivePlayer();
		g.getDetectives().add(dx);
		assertEquals(g, l.gs);
		assertEquals(4, l.i);
		assertEquals(dx, l.d);
		g.getDetectives().add(0, dy);
		assertEquals(0, l.i);
		assertEquals(dy, l.d);
		g.getDetectives().remove(dy);
		assertEquals(g, l.gs);
		assertEquals(0, l.i);
		assertEquals(dy, l.d);
		g.getDetectives().remove(4);
		assertEquals(4, l.i);
		assertEquals(dx, l.d);
		
	}
	
	@Test
	public final void testItemListener() {
		TestItemListener l = new TestItemListener();
		g.addItemListener(l);
		
		Set<Item> ims = new HashSet<>();
		ims.add(new DoubleMoveCard());
		
		g.setItems(d1, ims);
		assertEquals(g, l.gs);
		assertEquals(1, l.newI.size());
		assertEquals(d1, l.p);
		ims = l.newI;
		g.setItems(d1, null);
		assertEquals(ims, l.oldI);
		assertEquals(d1, l.p);
		assertEquals(null, l.newI);

		ims = new HashSet<>();
		ims.add(new DoubleMoveCard());
		ims.add(new TaxiTicket());
		ims.add(new BusTicket());
		g.setItems(mrX, ims);
		
		Item t = new UndergroundTicket();
		g.getItems(mrX).add(t);
		assertEquals(g, l.gs);
		assertEquals(mrX, l.p);
		assertEquals(t, l.i);
				
		g.getItems(mrX).remove(t);
		assertEquals(g, l.gs);
		assertEquals(mrX, l.p);
		assertEquals(t, l.i);
		
		g.getItems(mrX).clear();
		assertTrue(l.i != t); // weil t ja schon vorher geloescht wurde. is auch nur der beweis, dass der listener ueberhaupt bei clear aufgerufen wird.
		
	}
	
	@Test
	public final void testMoveListener() {
		g.getMoves().clear();
		TestMoveListener l = new TestMoveListener();
		g.addMoveListener(l);
		
		for (int i = 0; i < 7; i++) {
			l.gs = null;
			g.getMoves().add(ms[i]);
			assertEquals(g, l.gs);
			assertEquals(ms[i], l.m);		
		}
		
		for (int i = 6; i > 3; i--) {
			l.gs = null;
			g.getMoves().remove(GameState.LAST_MOVE);
			assertEquals(g, l.gs);
			assertEquals(ms[i], l.m);		
		}
		
		l.gs = null;
		g.getMoves().clear();
		assertEquals(g, l.gs);
		assertEquals(0, g.getMoves().size());
		
	}
	
}
