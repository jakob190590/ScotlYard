package kj.scotlyard.game.util;

import static org.junit.Assert.*;

import java.util.List;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.DefaultGame;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.TaxiTicket;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.rules.TheRules;

import org.junit.Before;
import org.junit.Test;

public class MrXTrackerTest {

	Game g;
	GameGraph gg;
	Rules r;
	GameStateExtension ext;
	MrXTracker tr;
	
	MrXPlayer mrX;
	DetectivePlayer d1, d2, d3, d4;
	Move[] ms = new Move[100];

	@Before
	public void setUp() throws Exception {
		g = new DefaultGame();
		r = new TheRules();
		ext = new GameStateExtension(g);
		tr = new MrXTracker(g, gg, r);
				
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
		
		SubMoves subMoves;

		// round number 1
		subMoves = new SubMoves()
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket())
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket());
		ms[5] = MoveProducer.createMultiMove(mrX, 1, 1, new DoubleMoveCard(), subMoves);

		// round number 2
		subMoves = new SubMoves()
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket())
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket());
		ms[10] = MoveProducer.createMultiMove(mrX, 2, 3, new DoubleMoveCard(), subMoves);
		
		// round number 5
		subMoves = new SubMoves()
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket())
				.add(new Station(gg), new TaxiConnection(gg), new TaxiTicket());
		ms[25] = MoveProducer.createMultiMove(mrX, 5, 7, new DoubleMoveCard(), subMoves);

		// Move numbers fuer mrX neu anpassen (wegen den nachtraeglichen multi moves)
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
	public final void testGetLastKnownMove() {
		
//		ListIterator<Move> it = ext.moveIterator(mrX, true);
//		while (it.hasNext()) {
//			Move m = it.next();
//			System.out.println(m.getRoundNumber() + "    " + m.getMoveNumber() + "     " + m.getMoveIndex());
//		}
		
		while (!g.getMoves().isEmpty()) {
			
			int moveNumber = ext.getLastMoveFlat(mrX).getMoveNumber();
			
			Move m = null;
			int uncover = -1;
			if (moveNumber >= 18) {
				m = ms[75];
				uncover = 18;
			} else if (moveNumber >= 13) {
				m = ms[50];
				uncover = 13;
			} else if (moveNumber >= 8) {
				m = ms[25].getMoves().get(1);
				uncover = 8;
			} else if (moveNumber >= 3) {
				m = ms[10].getMoves().get(0);
				uncover = 3;
			}
			
			if (moveNumber < 3) {
				assertEquals(null, tr.getLastKnownMove());
			} else {
				assertEquals(mrX, m.getPlayer());
				assertEquals(uncover, m.getMoveNumber());
				assertEquals(m, tr.getLastKnownMove());
			}
			
			g.getMoves().remove(GameState.LAST_MOVE);
		}
		
	}
	
	@Test
	public final void testGetMovesSince() {
		
		while (!g.getMoves().isEmpty()) {
			
			int moveNumber = ext.getLastMoveFlat(mrX).getMoveNumber();
			
			Move m = null;
			int uncover = -1;
			if (moveNumber >= 18) {
				m = ms[75];
				uncover = 18;
			} else if (moveNumber >= 13) {
				m = ms[50];
				uncover = 13;
			} else if (moveNumber >= 8) {
				m = ms[25].getMoves().get(1);
				uncover = 8;
			} else if (moveNumber >= 3) {
				m = ms[10].getMoves().get(0);
				uncover = 3;
			}
			
			List<Move> ts = tr.getMovesSince();
			
			if (moveNumber >= 3) {
				assertEquals(ext.getLastMoveFlat(mrX).getMoveNumber() - uncover, ts.size());
				if (ts.size() > 0) {
					assertEquals(ext.getLastMoveFlat(mrX), ts.get(ts.size() - 1));
				}
			}
			
			if (moveNumber < 3 || m == ext.getLastMoveFlat(mrX)) {
				// noch kein uncover move ODER genau auf uncover move
				
				assertTrue(ts.isEmpty());
				
			} else {
				
				// erstes ticket
				assertEquals(g.getMove(mrX, uncover + 1, GameState.MoveAccessMode.MOVE_NUMBER).getItem(), ts.get(0).getItem());
				
				// letztes ticket
				assertEquals(ext.getLastMoveFlat(mrX).getItem(), ts.get(ts.size() - 1).getItem());
				
				assertEquals(mrX, m.getPlayer());
				assertEquals(uncover, m.getMoveNumber());
				assertEquals(m, tr.getLastKnownMove());
			}			
			
			g.getMoves().remove(GameState.LAST_MOVE);
		}
		
		
		
	}

	@Test
	public final void testGetPossiblePositions() {
		// TODO implement test
		// Must be tested with a DetectivesGameState (GameStateAccessPolicy)!
		// Dabei darf es keine IllegalAccessExceptions geben!!
		
		// Testfaelle:
		// not yet uncovered, just uncovered, normal 
		fail("Not yet implemented");
	}

}
