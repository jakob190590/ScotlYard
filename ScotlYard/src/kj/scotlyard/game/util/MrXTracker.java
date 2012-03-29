package kj.scotlyard.game.util;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.items.Ticket;

public class MrXTracker {

	private GameState gameState;
	
	private GameGraph gameGraph;
	
	private GameStateExtension gameStateExtension;
	
	public MrXTracker(GameState gameState, GameGraph gameGraph) {
		super();
		this.gameState = gameState;
		this.gameGraph = gameGraph;
		gameStateExtension = new GameStateExtension(gameState);
	}

	public Move getLastKnownMove() {
		// TODO implement
		return null;
	}
	
	public Set<StationVertex> getPossiblePositions() {
		// ohne bewertung, denn das ist aufgabe der AI!
		// TODO implement
		return null;
	}
	
	public List<Ticket> getTicketsSince() {
		// ... Since last known Move, was sonst ...
		
		List<Ticket> list = new LinkedList<>();
		ListIterator<Move> it = gameStateExtension.moveIterator(gameState.getMrX(), getLastKnownMove().getRoundNumber());
		while (it.hasNext()) {
			list.add((Ticket) it.next().getItem()); // hier koennts cast exception geben, bei corrupt game state
		}
		return list;
	}
	
}
