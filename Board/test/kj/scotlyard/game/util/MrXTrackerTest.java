package kj.scotlyard.game.util;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import kj.scotlyard.board.BoardGraphLoader;
import kj.scotlyard.game.control.GameController;
import kj.scotlyard.game.control.impl.DefaultGameController;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.DefaultGame;
import kj.scotlyard.game.model.item.BlackTicket;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.TaxiTicket;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.rules.TheRules;

import org.junit.Before;
import org.junit.Test;

public class MrXTrackerTest {

	Game g;
	GameGraph gg;
	Rules r;
	GameStateExtension ext;
	GameState dgs;
	MrXTracker tr;
	Map<Integer, StationVertex> nsm;
	
	MrXPlayer mrX;
	DetectivePlayer d1, d2, d3, d4;
	Move[] ms = new Move[100];

	@Before
	public void setUp() throws Exception {
		
		BoardGraphLoader loader = new BoardGraphLoader();
		loader.load("graph-description", "initial-stations");
		gg = loader.getGameGraph();
		nsm = loader.getNumberStationMap();
		
		g = new DefaultGame();
		r = new TheRules();
		ext = new GameStateExtension(g);
		dgs = r.getGameStateAccessPolicy().createGameStateForDetectives(g);
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
		
		// Tracker sollte auch mit DetectivesGameState funktionieren!
		MrXTracker tr2 = new MrXTracker(dgs, gg, r);
		
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
				assertEquals(null, tr2.getLastKnownMove());
				
			} else {
				assertEquals(mrX, m.getPlayer());
				assertEquals(uncover, m.getMoveNumber());
				assertEquals(m, tr.getLastKnownMove());
				
				// MrXTracker mit Detective's GameState
				assertEquals(m.getStation(), tr2.getLastKnownMove().getStation());
				assertEquals(m.getItem(), tr2.getLastKnownMove().getItem());
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
	public final void testGetPossiblePositions1() {
		// Must be tested with a DetectivesGameState (GameStateAccessPolicy)!
		// Dabei darf es keine IllegalAccessExceptions geben!!
		
		// Testfaelle:
		// move list clear, not yet uncovered, just uncovered, normal 
		
		GameController ctrl = new DefaultGameController(g, gg, r);
		MrXTracker tr2 = new MrXTracker(dgs, gg, r);
		Set<StationVertex> poss;
		
		// -------------------------------------------
		// MoveList clear
		ctrl.newGame();
		try {
			tr.getPossiblePositions();
			fail("exception failed to appear");
		} catch (IllegalStateException e) { }
		try {
			tr2.getPossiblePositions();
			fail("exception failed to appear");
		} catch (IllegalStateException e) { }
		

		// only initial moves (not yet uncovered)
		ctrl.start();
		poss = tr.getPossiblePositions();
		assertEquals(gg.getInitialStations().size() - g.getDetectives().size(), poss.size());
//		assertEquals(new HashSet<>(gg.getInitialStations()).removeAll(ext.getDetectivePositions()), poss); // scheitert; vllt wg. der reihenfolge
		for (StationVertex s : poss) {
			assertTrue(gg.getInitialStations().contains(s));
		}
		poss = tr2.getPossiblePositions();
		assertEquals(gg.getInitialStations().size() - g.getDetectives().size(), poss.size());
//		assertEquals(new HashSet<>(gg.getInitialStations()).removeAll(ext.getDetectivePositions()), poss); // scheitert; vllt wg. der reihenfolge
		for (StationVertex s : poss) {
			assertTrue(gg.getInitialStations().contains(s));
		}
		
		// -------------------------------------------
		// just uncovered
		while (ctrl.getWin() == GameWin.NO) {
			ctrl.move(MoveProducer.createNextBestSingleMove(g, gg));
			
			Move lastMrXMove = g.getLastMove(mrX);
			if (r.getGameStateAccessPolicy().getMrXUncoverMoveNumbers()
					.contains(lastMrXMove.getMoveNumber())) {
				
				poss = tr.getPossiblePositions();
				assertEquals(1, poss.size());
				assertEquals(g.getLastMove(mrX).getStation(), poss.iterator().next());
			}
		}
		
		// -------------------------------------------
		// one moves after uncover
		ctrl.newGame();
		
		for (int i = 0; i < 3; i++) { // zwei runden iwelche moves adden
			for (Player pl : g.getPlayers()) {
				g.getMoves().add(MoveProducer.createInitialMove(pl, i, nsm.get(11)));
			}
		}

		// jetzt kommt mrx uncover move und gleich noch einer:
		g.getMoves().add(MoveProducer.createMultiMove(
				mrX, 3, 3, new DoubleMoveCard(), new SubMoves()
						.add(nsm.get(112), null, new TaxiTicket()) // uncover move
						.add(nsm.get(111), null, new TaxiTicket()))); // naechster move
		
		// Die Detectives setzen wir jetzt wo ganz woanders hin:
		g.getMoves().add(MoveProducer.createInitialMove(d1, 3, nsm.get(1)));
		g.getMoves().add(MoveProducer.createInitialMove(d2, 3, nsm.get(2)));
		g.getMoves().add(MoveProducer.createInitialMove(d3, 3, nsm.get(3)));
		g.getMoves().add(MoveProducer.createInitialMove(d4, 3, nsm.get(4)));
		
		poss = tr.getPossiblePositions();
		tr2.getPossiblePositions();
		assertTrue(poss.contains(nsm.get(111)));
		assertTrue(poss.contains(nsm.get(99)));
		assertTrue(poss.contains(nsm.get(125)));
		assertTrue(poss.contains(nsm.get(100)));
		assertEquals(4, poss.size());
			
		// -------------------------------------------
		// two moves after uncover
		g.getMoves().add(MoveProducer.createSingleMove(mrX,
				4, 5, nsm.get(124), null, new BlackTicket()));
		poss = tr.getPossiblePositions();
		tr2.getPossiblePositions();
		// von 111 aus
		assertTrue(poss.contains(nsm.get(110)));
		assertTrue(poss.contains(nsm.get(79)));
		assertTrue(poss.contains(nsm.get(124)));
		assertTrue(poss.contains(nsm.get(112)));
		assertTrue(poss.contains(nsm.get(163)));
		assertTrue(poss.contains(nsm.get(153)));
		assertTrue(poss.contains(nsm.get(67)));
		assertTrue(poss.contains(nsm.get(100)));
		// von 99 aus
		assertTrue(poss.contains(nsm.get(80)));
		assertTrue(poss.contains(nsm.get(98)));
		// von 100 aus
		assertTrue(poss.contains(nsm.get(111)));
		assertTrue(poss.contains(nsm.get(113)));
		assertTrue(poss.contains(nsm.get(81)));
		assertTrue(poss.contains(nsm.get(101)));
		assertTrue(poss.contains(nsm.get(82)));
		assertTrue(poss.contains(nsm.get(63)));
		// von 125 aus
		assertTrue(poss.contains(nsm.get(131)));
		
		assertEquals(17, poss.size());
	}
	
	/**
	 * Dieser Test blockiert bestimmte Stationen in
	 * bestimmenten Runden mit Detectives. Ansonsnten
	 * das gleiche Szenario wie in Test 1. 
	 */
	@Test
	public final void testGetPossiblePositions2() {

		MrXTracker tr2 = new MrXTracker(dgs, gg, r);
		Set<StationVertex> poss;
		g.getMoves().clear();
		
		// -------------------------------------------
		// one moves after uncover
		
		for (int i = 0; i < 2; i++) { // zwei runden iwelche moves adden
			for (Player pl : g.getPlayers()) {
				g.getMoves().add(MoveProducer.createInitialMove(pl, i, nsm.get(11)));
			}
		}
		
		g.getMoves().add(MoveProducer.createInitialMove(mrX, 2, nsm.get(99)));
		g.getMoves().add(MoveProducer.createInitialMove(d1, 2, nsm.get(100))); // der blockiert 100
		g.getMoves().add(MoveProducer.createInitialMove(d2, 2, nsm.get(131)));
		g.getMoves().add(MoveProducer.createInitialMove(d3, 2, nsm.get(3)));
		g.getMoves().add(MoveProducer.createInitialMove(d4, 2, nsm.get(4)));

		// jetzt kommt mrx uncover move und gleich noch einer:
		g.getMoves().add(MoveProducer.createMultiMove(
				mrX, 3, 3, new DoubleMoveCard(), new SubMoves()
						.add(nsm.get(112), null, new TaxiTicket()) // uncover move
						.add(nsm.get(111), null, new TaxiTicket()))); // naechster move
		
		poss = tr.getPossiblePositions();
		tr2.getPossiblePositions();
		assertTrue(poss.contains(nsm.get(111)));
		assertTrue(poss.contains(nsm.get(99)));
		assertTrue(poss.contains(nsm.get(125)));
		assertEquals(3, poss.size());

		// det. moves
		g.getMoves().add(MoveProducer.createInitialMove(d1, 3, nsm.get(81)));
		g.getMoves().add(MoveProducer.createInitialMove(d2, 3, nsm.get(125)));
		g.getMoves().add(MoveProducer.createInitialMove(d3, 3, nsm.get(3)));
		g.getMoves().add(MoveProducer.createInitialMove(d4, 3, nsm.get(4)));
		
		poss = tr.getPossiblePositions();
		tr2.getPossiblePositions();
		assertTrue(poss.contains(nsm.get(111)));
		assertTrue(poss.contains(nsm.get(99)));
		assertEquals(2, poss.size());
			
		// -------------------------------------------
		// two moves after uncover
		g.getMoves().add(MoveProducer.createSingleMove(mrX,
				4, 5, nsm.get(124), null, new BlackTicket()));
		poss = tr.getPossiblePositions();
		tr2.getPossiblePositions();
		// von 111 aus
		assertTrue(poss.contains(nsm.get(110)));
		assertTrue(poss.contains(nsm.get(79)));
		assertTrue(poss.contains(nsm.get(124)));
		assertTrue(poss.contains(nsm.get(112)));
		assertTrue(poss.contains(nsm.get(163)));
		assertTrue(poss.contains(nsm.get(153)));
		assertTrue(poss.contains(nsm.get(67)));
		assertTrue(poss.contains(nsm.get(100)));
		// von 99 aus
		assertTrue(poss.contains(nsm.get(80)));
		assertTrue(poss.contains(nsm.get(98)));
		
		assertEquals(10, poss.size());
		
		/*
		 * Bugfixed:
		 * 131 u. 113 waren iwie nicht dabei
		 * d2 pos (125) in round 3 wurde nicht honoriert wenn mrx erneut gezogen hat
		 */
		printStationNumbers(poss, nsm);
	}

	private void printStationNumbers(Set<StationVertex> stations, Map<Integer, StationVertex> map) {
		for (StationVertex s : stations) {
			for (Map.Entry<Integer, StationVertex> e : map.entrySet()) {
				if (e.getValue().equals(s))
					System.out.println(e.getKey());
			}
		}
	}

}
