package kj.scotlyard.game.util;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import kj.scotlyard.game.graph.Connection;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.DefaultGame;
import kj.scotlyard.game.model.item.BusTicket;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.TaxiTicket;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.model.item.UndergroundTicket;

import org.junit.Before;
import org.junit.Test;

public class GameStateExtensionTest {
	
	Game g;
	GameStateExtension ext;
	GameGraph gg = null;
	
	MrXPlayer mrX;
	DetectivePlayer d1, d2, d3, d4;
	Move[] ms = new Move[100];
	
	SubMoves subMoves;

	@Before
	public void setUp() throws Exception {
		g = new DefaultGame();
		ext = new GameStateExtension(g);
				
		Move m2;
		
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
				ms[j] = MoveProducer.createSingleMove(p, i, i,
						new Station(gg), new TaxiConnection(gg), new TaxiTicket());
				j++;
			}
		}

		subMoves = new SubMoves()
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket())
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket());
		m2 = ms[5] = MoveProducer.createMultiMove(mrX, 1, 1, new DoubleMoveCard(), subMoves);
		m2.getMoves().get(0);
		m2 = m2.getMoves().get(1);

		subMoves = new SubMoves()
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket())
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket());
		m2 = ms[15] = MoveProducer.createMultiMove(mrX, 3, 4, new DoubleMoveCard(), subMoves);
		m2.getMoves().get(0);
		m2 = m2.getMoves().get(1);

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
		
	}

	@Test
	public final void testGetMovesFlat() { }

	@Test
	public final void testMoveIteratorPlayerBooleanInt() {
		ListIterator<Move> it;
		Move m;
		
		// nicht flat iterieren
		int pi = 0;
		for (Player p : g.getPlayers()) {
			
			for (int j = -2; j <= 2; j++) {
				
				for (int k = 0; k < 2; k++) {
					
					if (k == 1 && p == mrX)
						continue;
				
					it = ext.moveIterator(p, k == 1, j);
					
					int i = (j < 0) ? 0 : j;
					while (it.hasNext()) {
						m = ms[pi + 5 * i];
						
						assertEquals(m, it.next());
						assertEquals(i, m.getRoundNumber());
						
						if (k == 1)
							assertEquals(i, m.getMoveNumber());
							
						
						assertTrue(it.hasPrevious());
						assertEquals(m, it.previous());
						
						it.next();
						
						i++;
					}
					
					assertEquals(20, i);
					
					while (it.hasPrevious()) {
						i--;
						m = ms[pi + 5 * i];
						
						assertEquals(m, it.previous());
						assertEquals(i, m.getRoundNumber());
						
						if (k == 1)
							assertEquals(i, m.getMoveNumber());
						
						assertTrue(it.hasNext());
						assertEquals(m, it.next());
						
						it.previous();
					}
					
					assertEquals(0, i);
					
					for (int l = 20; l < 23; l++) {						
						
						it = ext.moveIterator(p, false, l);
						assertTrue(!it.hasNext());
						assertTrue(it.hasPrevious());
						assertEquals(g.getLastMove(p), it.previous());
						assertEquals(g.getMove(p, -2, GameState.MoveAccessMode.ROUND_NUMBER), it.previous());
						assertEquals(g.getMove(p, -3, GameState.MoveAccessMode.ROUND_NUMBER), it.previous());
						assertEquals(g.getMove(p, -3, GameState.MoveAccessMode.ROUND_NUMBER), it.next());
						assertEquals(g.getMove(p, -2, GameState.MoveAccessMode.ROUND_NUMBER), it.next());
						assertEquals(g.getMove(p, -1, GameState.MoveAccessMode.ROUND_NUMBER), it.next());
						assertTrue(!it.hasNext());				
						
					}
				}
				
			}
			
			pi++;
			
		}
		
		// mrx flat iterator testen
		it = ext.moveIterator(mrX, true, 0);
		int i = 0;
		while (it.hasNext()) {
			it.next();
			i++;
		}
		assertEquals(22, i); // weil er zwei double moves dabei hat.
		
	}

	@Test
	public final void testMoveIteratorPlayerBoolean() {
		ListIterator<Move> it;
		Move m;
		
		// nicht flat iterieren
		int pi = 0;
		for (Player p : g.getPlayers()) {
			
			it = ext.moveIterator(p, false);
			
			int i = 0;
			while (it.hasNext()) {
				m = ms[pi + 5 * i];
				
				assertEquals(m, it.next());
				
				assertTrue(it.hasPrevious());
				assertEquals(m, it.previous());
				
				it.next();
				
				i++;
			}
			
			assertEquals(20, i);
			
			while (it.hasPrevious()) {
				i--;
				m = ms[pi + 5 * i];
				
				assertEquals(m, it.previous());
				
				assertTrue(it.hasNext());
				assertEquals(m, it.next());
				
				it.previous();
			}
			
			assertEquals(0, i);
			
			pi++;
		}
		
		
		// tests fuer mrX
		it = ext.moveIterator(mrX, false);
		for (int i = 0; i < 100; i += 5) {
			assertEquals(ms[i], it.next());
		}
		assertTrue(!it.hasNext());
				
		assertTrue(it.hasPrevious());
		assertEquals(ms[95], it.previous());		
		// next = letztes
		// previous = vorletztes
		// PASST
		
		assertTrue(it.hasNext());
		assertEquals(ms[95], it.next());
		// next = null
		// previous = letztes
		// PASST
		
		assertTrue(!it.hasNext());
		
		assertTrue(it.hasPrevious());
		assertEquals(ms[95], it.previous());
		// next = letztes
		// previous = vorletztes
		// PASST
		
		assertTrue(it.hasPrevious());
		assertEquals(ms[90], it.previous());
		// next = vorletztes
		// previous = vorvorletztes		
		// PASST
		
		assertTrue(it.hasPrevious());
		assertEquals(ms[85], it.previous());
		
		assertTrue(it.hasNext());
		assertEquals(ms[85], it.next());
		
		assertTrue(it.hasNext());
		assertEquals(ms[90], it.next());
		
		while (it.hasPrevious()) {
			it.previous();
		}
		assertEquals(ms[0], it.next());
		assertEquals(ms[5], it.next());
		assertEquals(ms[10], it.next());
		assertEquals(ms[10], it.previous());
		assertEquals(ms[5], it.previous());
		assertEquals(ms[0], it.previous());
		assertTrue(!it.hasPrevious());
	}

	@Test
	public final void testGetMoves() { }

	@Test
	public final void testGetMove() { }

	@Test
	public final void testGetLastMoveFlat() {
		Move m1, m2, m3;
		
		subMoves = new SubMoves()
				.add(new Station(gg), new BusConnection(gg), new BusTicket())
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket());
		g.getMoves().add(m1 = MoveProducer.createMultiMove(mrX, 20, 22, new DoubleMoveCard(), subMoves));
		
		subMoves = new SubMoves()
				.add(new Station(gg), new BusConnection(gg), new BusTicket())
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket());
		g.getMoves().add(m2 = MoveProducer.createMultiMove(d1, 20, 20, new DoubleMoveCard(), subMoves));
		
		g.getMoves().add(m3 = MoveProducer.createSingleMove(d2, 20, 20, new Station(gg), new UndergroundConnection(gg), new UndergroundTicket()));
		
		assertEquals(m1.getMoves().get(1), ext.getLastMoveFlat(mrX));
		assertEquals(m2.getMoves().get(1), ext.getLastMoveFlat(d1));
		assertEquals(m3, ext.getLastMoveFlat(d2));
	}

	@Test
	public final void testFlattenMove() { 
		
		subMoves = new SubMoves()
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket())
				.add(new Station(gg), new BusConnection(gg), new BusTicket())
				.add(new Station(gg), new UndergroundConnection(gg), new UndergroundTicket());
		Move m = MoveProducer.createMultiMove(new MrXPlayer(), 4, 4, new DoubleMoveCard(), subMoves);
		
		List<Move> ms;
		
		// do include base move(s)
		ms = ext.flattenMove(m, true); 
		assertEquals(4, ms.size());
		
		// do omit base move(s)
		ms = ext.flattenMove(m, false);
		assertEquals(3, ms.size());
		
		
		// ------------------------------
		// noch tiefere hierarchie testen
		
		Player p = new MrXPlayer();
		Station s = new Station(gg);
		Ticket t = new TaxiTicket();
		Connection c = new TaxiConnection(gg);
		
		Move m00 = MoveProducer.createInitialMove(p, s);
		Move m01 = MoveProducer.createInitialMove(p, s);
		Move m0 = new DefaultMove(p, 0, 0, 0, s, c, t, m00, m01); 
		
		Move m1 = MoveProducer.createInitialMove(p, s);
		
		Move m20 = MoveProducer.createInitialMove(p, s);
		Move m21 = MoveProducer.createInitialMove(p, s);
		Move m2 = new DefaultMove(p, 0, 0, 0, s, c, t, m20, m21);
		
		m = new DefaultMove(p, 0, 0, 0, s, c, t, m0, m1, m2);
		
		// do include base move(s)
		ms = ext.flattenMove(m, true); 
		assertEquals(8, ms.size());
		
		// do omit base move(s)
		ms = ext.flattenMove(m, false);
		assertEquals(5, ms.size());
	}

	@Test
	public final void testGetItem() { }
	
	@Test
	public final void testGetDetectivePositionsInt() {
		
		for (int i = -3; i < 0; i++)
			try {
				ext.getDetectivePositions(i);
				fail("exception failed to appear");
			} catch (IllegalArgumentException e) { }
		
		StationVertex[] s = {
				new Station(gg),
				new Station(gg),
				new Station(gg),
				new Station(gg),
				new Station(gg),
				new Station(gg),
				new Station(gg),
				new Station(gg) };
		
		// Alle Detectives haben letzte Runde gezogen
		int rNbr = g.getLastMove(d4).getRoundNumber() + 1;
		int i = 0;
		for (Player pl : g.getPlayers()) {
			g.getMoves().add(MoveProducer.createInitialMove(pl, rNbr, s[i++]));
		}
		
		Set<StationVertex> detectivePositions = ext.getDetectivePositions(rNbr);
		assertEquals(g.getDetectives().size(), detectivePositions.size());
		for (i = 1; i <= 4; i++) {
			assertTrue(detectivePositions.contains(s[i]));
		}
		
		// Detectives haben unterschiedlich weit gezogen
		rNbr++;
		i = 5;
		for (Player pl : g.getPlayers()) {
			g.getMoves().add(MoveProducer.createInitialMove(pl, rNbr, s[i++]));
			if (i > 7)
				break;
		}
		
		detectivePositions = ext.getDetectivePositions(rNbr);
		assertEquals(g.getDetectives().size(), detectivePositions.size());
		for (i = 3; i < 8; i++) {
			if (i != 5) // mrx auslassen
				assertTrue(detectivePositions.contains(s[i++]));
		}
		
		// Ein Detective weniger im Spiel
		g.getDetectives().remove(2);
		assertEquals(g.getDetectives().size(), ext.getDetectivePositions(rNbr).size());
		
		// Kein einziger hat gezogen
		g.getMoves().clear();
		assertTrue(ext.getDetectivePositions(1).isEmpty());
	}
	
	@Test
	public final void testGetDetectivePositions() {
		
		StationVertex[] s = {
				new Station(gg),
				new Station(gg),
				new Station(gg),
				new Station(gg),
				new Station(gg),
				new Station(gg),
				new Station(gg),
				new Station(gg) };
		
		// Alle Detectives haben letzte Runde gezogen
		int i = 0;
		for (Player pl : g.getPlayers()) {
			g.getMoves().add(MoveProducer.createInitialMove(pl, s[i++]));
		}
		
		Set<StationVertex> detectivePositions = ext.getDetectivePositions();
		assertEquals(g.getDetectives().size(), detectivePositions.size());
		for (i = 1; i <= 4; i++) {
			assertTrue(detectivePositions.contains(s[i]));
		}
		
		// Detectives haben unterschiedlich weit gezogen
		i = 5;
		for (Player pl : g.getPlayers()) {
			g.getMoves().add(MoveProducer.createInitialMove(pl, s[i++]));
			if (i > 7)
				break;
		}
		
		detectivePositions = ext.getDetectivePositions();
		assertEquals(g.getDetectives().size(), detectivePositions.size());
		for (i = 3; i < 8; i++) {
			if (i != 5) // mrx auslassen
				assertTrue(detectivePositions.contains(s[i++]));
		}
		
		// Ein Detective weniger im Spiel
		g.getDetectives().remove(2);
		assertEquals(g.getDetectives().size(), ext.getDetectivePositions().size());
		
		// Kein einziger hat gezogen
		g.getMoves().clear();
		assertTrue(ext.getDetectivePositions().isEmpty());
	}

}
