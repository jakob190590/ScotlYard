package kj.scotlyard.game.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.rules.Rules;

public class MrXTracker {

	private final GameState gameState;
	
	private final GameGraph gameGraph;
	
	private final Rules rules;
	
	private final GameStateExtension gameStateExtension;
	
	public MrXTracker(GameState gameState, GameGraph gameGraph, Rules rules) {
		this.gameState = gameState;
		this.gameGraph = gameGraph;
		this.rules = rules;
		gameStateExtension = new GameStateExtension(gameState);
	}

	/**
	 * Gibt den Zug zurueck, an dem MrX
	 * das letzte Mal aufgetaucht ist,
	 * oder <tt>null</tt>, wenn MrX noch
	 * nicht aufgetaucht ist.
	 * @return MrX' last uncovered move,
	 * or <tt>null</tt> if MrX wasn't
	 * uncovered yet.
	 */
	public Move getLastKnownMove() {
		Player mrX = gameState.getMrX();
		Move lastMove = gameStateExtension.getLastMoveFlat(mrX);
		
		List<Integer> uncover = rules.getGameStateAccessPolicy().getMrXUncoverMoveNumbers();
		ListIterator<Integer> it = uncover.listIterator(uncover.size());
		while (it.hasPrevious()) {
			int n = it.previous();
			if (lastMove.getMoveNumber() >= n) {
				return gameState.getMove(mrX, n, GameState.MoveAccessMode.MOVE_NUMBER);
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the List of MrX' Moves since his last uncovered
	 * Move. The uncovered Move is excluded. If there was no
	 * uncovered Move yet (<tt>getLastKnownMove() == null</tt>),
	 * or if there was no Move since the last uncovered Move,
	 * the List is empty.
	 * @return the List of MrX' Moves since his last uncovered Move.
	 */
	public List<Move> getMovesSince() {
		// ... Since: Last known Move (was sonst)
		
		List<Move> list = new LinkedList<>();
		
		Move lastUncovered = getLastKnownMove();
		
		if (lastUncovered != null) {
			
			ListIterator<Move> it = gameStateExtension.moveIterator(
					gameState.getMrX(), true, lastUncovered.getMoveNumber() + 1);
			
			while (it.hasNext()) {
				list.add(it.next());
			}
		}
		return list;
	}
		
	/**
	 * Calculates all possible Positions of MrX on the current
	 * GameState.
	 * @return a Set of all possible Positions of MrX.
	 */
	public Set<StationVertex> getPossiblePositions() {
		// ohne bewertung, denn das ist aufgabe der AI!
		
		// wenn die AI diese methode nicht nutzen will, ist mir das auch egal.
		// die GUI kann sie auf jeden fall brauchen.
		
		Iterator<Move> it;
		Set<StationVertex> result;
		
		Move last = getLastKnownMove();
		if (last == null) {
			// MrX wasn't uncovered yet, so every position is possible
			it = gameStateExtension.moveIterator(gameState.getMrX(), true);
			if (it.hasNext()) {
				it.next();
			} else {
				throw new IllegalStateException("MrX has no initial move yet.");
			}
			result = new HashSet<>(gameGraph.getInitialStations());
			result.removeAll(gameStateExtension.getDetectivePositions(GameState.INITIAL_ROUND_NUMBER));
		} else {
			it = getMovesSince().iterator();
			result = Collections.singleton(last.getStation());			
		}
		
		while (it.hasNext()) {

			Move move = it.next();
			Ticket ticket = (Ticket) move.getItem();
			
			Set<StationVertex> stationSet = new HashSet<>();
			Set<StationVertex> detectiveStationSet = gameStateExtension
					.getDetectivePositions(move.getRoundNumber());
			
			for (StationVertex station : result) {
				
				for (ConnectionEdge connection : station.getEdges()) {
					
					StationVertex s = connection.getOther(station);
					
					if (rules.getMovePolicy().isTicketValidForConnection(ticket, connection)
							&& !detectiveStationSet.contains(s)) {
						
						stationSet.add(connection.getOther(station));
					}
				}
			}
			
			result = stationSet;
			
		}
		
		return result;
	}
	
}
