package kj.scotlyard.game.model;

import static org.junit.Assert.*;
import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.model.items.BlackTicket;
import kj.scotlyard.game.model.items.DoubleMoveCard;
import kj.scotlyard.game.model.items.Item;

import org.junit.Test;

public class MoveTest {

	@Test
	public void testSeal() {
		Move m = new TheMove(new DetectivePlayer(), new StationVertex());
		m.seal();
		
		try {
			m.setConnection(new BusConnection());
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
			m.setStation(new StationVertex());
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
		StationVertex s = new StationVertex();
		
		Move m = new TheMove(p, s);
		
		assertEquals(p, m.getPlayer());
		assertEquals(s, m.getStation());
	}

	@Test
	public void testTheMovePlayerConnectionEdgeStationVertexItem() {
		Player p = new DetectivePlayer();
		StationVertex s = new StationVertex();
		ConnectionEdge c = new FerryConnection();
		Item i = new BlackTicket();
		
		Move m = new TheMove(p,c,s,i);
		
		assertEquals(p, m.getPlayer());
		assertEquals(s, m.getStation());
		assertEquals(i, m.getItem());
		assertEquals(c, m.getConnection());
	}

	@Test
	public void testTheMovePlayerItemMoveArray() {
		
		Move m1 = new TheMove(new DetectivePlayer(), new StationVertex());

		Move m2 = new TheMove(new DetectivePlayer(), new FerryConnection(), new StationVertex(), new BlackTicket());
		
		
		Player p = new DetectivePlayer();
		Item i = new DoubleMoveCard();
		
		Move m = new TheMove(p, i, m1, m2);
		assertEquals(i, m.getItem());
		assertEquals(p, m.getPlayer());
		assertEquals(2, m.getMoves().size());
		assertEquals(m1, m.getMoves().get(0));
		assertEquals(m2, m.getMoves().get(1));
	}
	
	@Test
	public void testSetGet() {
		Move m = new TheMove(new DetectivePlayer(), new StationVertex());
		
		ConnectionEdge c = new BusConnection();
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
	
		StationVertex s = new StationVertex(); 
		m.setStation(s);
		assertEquals(s, m.getStation());
	
		m.getMoves().add(m);
		assertEquals(m, m.getMoves().get(0));
				
	}

}
