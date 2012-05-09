package kj.scotlyard.game.util;

import java.util.HashSet;
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

	private GameState gameState;
	
	private GameGraph gameGraph;
	
	private Rules rules;
	
	private GameStateExtension gameStateExtension;
	
	public MrXTracker(GameState gameState, GameGraph gameGraph, Rules rules) {
		this.gameState = gameState;
		this.gameGraph = gameGraph;
		gameStateExtension = new GameStateExtension(gameState);
		this.rules = rules;
	}

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
		
	public Set<StationVertex> getPossiblePositions() {
		// ohne bewertung, denn das ist aufgabe der AI!
		
		// wenn die AI diese methode nicht nutzen will, ist mir das auch egal.
		// die GUI kann sie auf jeden fall brauchen.
		
		Set<StationVertex> result = new HashSet<>();
		
		result.add(getLastKnownMove().getStation());
		
		for (Move move : getMovesSince()) {
		
			Ticket ticket = (Ticket) move.getItem();
			
			Set<StationVertex> stationSet = new HashSet<>();
			Set<StationVertex> detectiveStationSet = new HashSet<>();
			
			for (DetectivePlayer d : gameState.getDetectives()) {
				Move m = gameState.getMove(d, move.getRoundNumber(),
						GameState.MoveAccessMode.ROUND_NUMBER);
				if (m != null)
					detectiveStationSet.add(m.getStation());
			}
			
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
