package kj.scotlyard.game.model;

import static org.junit.Assert.*;
import kj.scotlyard.game.graph.Connection;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.model.item.BlackTicket;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.Item;

import org.junit.Test;

public class DefaultMoveTest {
	
	TheMoveProducer prod = TheMoveProducer.createInstance();
	
	GameGraph gg = null;
	
	@Test
	public void testSeal() {
		Move m = new DefaultMove();
		m.seal();
		
		try {
			m.setConnection(new BusConnection(gg));
			fail("method should be sealed, but is not.");
		} catch (Exception e) { }
		
		try {
			m.setItem(new DoubleMoveCard());
			fail("method should be sealed, but is not.");
		} catch (Exception e) { }
		
		try {
			m.setMoveIndex(0);
			fail("method should be sealed, but is not.");
		} catch (Exception e) { }
		
		try {
			m.setMoveNumber(GameState.INITIAL_MOVE_NUMBER);
			fail("method should be sealed, but is not.");
		} catch (Exception e) { }
		
		try {
			m.setPlayer(new MrXPlayer());
			fail("method should be sealed, but is not.");
		} catch (Exception e) { }
		
		try {
			m.setRoundNumber(GameState.INITIAL_ROUND_NUMBER);
			fail("method should be sealed, but is not.");
		} catch (Exception e) { }
		
		try {
			m.setStation(new Station(gg));
			fail("method should be sealed, but is not.");
		} catch (Exception e) { }
		
		try {
			m.getMoves().add(m);			
			fail("method should be sealed, but is not.");
		} catch (Exception e) { }
		
		try {
			m.getMoves().remove(m);			
			fail("method should be sealed, but is not.");
		} catch (Exception e) { }
		
		try {
			m.getMoves().clear();			
			fail("method should be sealed, but is not.");
		} catch (Exception e) { }
	}

	@Test
	public void testTheMovePlayerStationVertex() {
		Player p = new DetectivePlayer();
		Station s = new Station(gg);
		
		Move m = new DefaultMove();
		m.setPlayer(p);
		m.setStation(s);
		
		assertEquals(p, m.getPlayer());
		assertEquals(s, m.getStation());
	}

	@Test
	public void testTheMovePlayerConnectionEdgeStationVertexItem() {
		Player p = new DetectivePlayer();
		Station s = new Station(gg);
		Connection c = new FerryConnection(gg);
		Item i = new BlackTicket();
		
		Move m = new DefaultMove();
		m.setPlayer(p);
		m.setStation(s);
		m.setConnection(c);
		m.setItem(i);
		
		assertEquals(p, m.getPlayer());
		assertEquals(s, m.getStation());
		assertEquals(i, m.getItem());
		assertEquals(c, m.getConnection());
	}

	@Test
	public void testTheMovePlayerItemMoveArray() {
		
		Move m1 = new DefaultMove();

		Move m2 = new DefaultMove();
		
		
		Player p = new DetectivePlayer();
		Item i = new DoubleMoveCard();
		
		Move m = new DefaultMove(p, 2, 5, 3, null, null, i, m1, m2);
		assertEquals(i, m.getItem());
		assertEquals(p, m.getPlayer());
		assertEquals(2, m.getMoves().size());
		assertEquals(m1, m.getMoves().get(0));
		assertEquals(m2, m.getMoves().get(1));
	}
	
	@Test
	public void testSetGet() {
		Move m = new DefaultMove();
		
		Connection c = new BusConnection(gg);
		m.setConnection(c);
		assertEquals(c, m.getConnection());
	
		Item i = new DoubleMoveCard();
		m.setItem(i);
		assertEquals(i, m.getItem());
		
		m.setMoveIndex(0);
		assertEquals(0, m.getMoveIndex());
	
		m.setMoveNumber(1);
		assertEquals(1, m.getMoveNumber());
		
		Player p = new MrXPlayer();
		m.setPlayer(p);
		assertEquals(p, m.getPlayer());
	
		m.setRoundNumber(1);
		assertEquals(1, m.getRoundNumber());
	
		Station s = new Station(gg); 
		m.setStation(s);
		assertEquals(s, m.getStation());
	
		m.getMoves().add(m);
		assertEquals(m, m.getMoves().get(0));
				
	}

}
