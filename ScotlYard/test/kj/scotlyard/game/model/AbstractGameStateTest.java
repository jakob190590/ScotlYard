package kj.scotlyard.game.model;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.model.items.BusTicket;
import kj.scotlyard.game.model.items.DoubleMoveCard;
import kj.scotlyard.game.model.items.Item;
import kj.scotlyard.game.model.items.TaxiTicket;
import kj.scotlyard.game.model.items.UndergroundTicket;

import org.junit.Before;
import org.junit.Test;

public class AbstractGameStateTest {
	
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
	GameState gs;
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
		
		
		
		
		
		
		
		gs = new FullGameState(g);
	}

	@Test
	public final void testStateListener() {
		TestStateListener l = new TestStateListener();
		gs.addStateListener(l);
		
		g.setCurrentPlayer(null);
		assertEquals(null, l.newP);
		assertEquals(gs, l.gs);
		g.setCurrentPlayer(d3);
		assertEquals(null, l.oldP);
		assertEquals(d3, l.newP);
		g.setCurrentPlayer(null);
		assertEquals(d3, l.oldP);
		assertEquals(null, l.newP);
		
		
		g.setCurrentRoundNumber(2);
		assertEquals(2, l.newR);
		assertEquals(gs, l.gs);
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
		gs.addPlayerListener(l);
		
		g.setMrX(null);
		assertEquals(null, l.newX);
		assertEquals(gs, l.gs);
		g.setMrX(mrX);
		assertEquals(null, l.oldX);
		assertEquals(mrX, l.newX);
		g.setMrX(null);
		assertEquals(mrX, l.oldX);
		assertEquals(null, l.newX);
		
		DetectivePlayer dx = new DetectivePlayer();
		DetectivePlayer dy = new DetectivePlayer();
		g.getDetectives().add(dx);
		assertEquals(gs, l.gs);
		assertEquals(4, l.i);
		assertEquals(dx, l.d);
		g.getDetectives().add(0, dy);
		assertEquals(0, l.i);
		assertEquals(dy, l.d);
		g.getDetectives().remove(dy);
		assertEquals(gs, l.gs);
		assertEquals(0, l.i);
		assertEquals(dy, l.d);
		g.getDetectives().remove(4);
		assertEquals(4, l.i);
		assertEquals(dx, l.d);
		
	}
	
	@Test
	public final void testItemListener() {
		TestItemListener l = new TestItemListener();
		gs.addItemListener(l);
		
		Set<Item> ims = new HashSet<>();
		ims.add(new DoubleMoveCard());
		
		g.setItems(d1, ims);
		assertEquals(gs, l.gs);
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
		assertEquals(gs, l.gs);
		assertEquals(mrX, l.p);
		assertEquals(t, l.i);
				
		g.getItems(mrX).remove(t);
		assertEquals(gs, l.gs);
		assertEquals(mrX, l.p);
		assertEquals(t, l.i);
		
		g.getItems(mrX).clear();
		assertTrue(l.i != t); // weil t ja schon vorher geloescht wurde. is auch nur der beweis, dass der listener ueberhaupt bei clear aufgerufen wird.
		
	}
	
	@Test
	public final void testMoveListener() {
		TestMoveListener l = new TestMoveListener();
		gs.addMoveListener(l);
		
		for (int i = 0; i < 7; i++) {
			l.gs = null;
			g.getMoves().add(ms[i]);
			assertEquals(gs, l.gs);
			assertEquals(ms[i], l.m);		
		}
		
		for (int i = 6; i > 3; i--) {
			l.gs = null;
			g.getMoves().remove(GameState.LAST_MOVE);
			assertEquals(gs, l.gs);
			assertEquals(ms[i], l.m);		
		}
		
		l.gs = null;
		g.getMoves().clear();
		assertEquals(gs, l.gs);
		assertEquals(0, g.getMoves().size());
		
	}

}
