package kj.scotlyard.game.util;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.items.Ticket;
import kj.scotlyard.game.rules.Rules;

public class MrXTracker {

	private GameState gameState;
	
	private GameGraph gameGraph;
	
	private Rules rules;
	
	private GameStateExtension gameStateExtension;
	
	public MrXTracker(GameState gameState, GameGraph gameGraph, Rules rules) {
		super();
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
	
	public Set<StationVertex> getPossiblePositions() {
		// ohne bewertung, denn das ist aufgabe der AI!
		
		// TODO implement
		return null;
	}
	
	public List<Ticket> getTicketsSince() {
		// ... Since last known Move, was sonst ...
		
		List<Ticket> list = new LinkedList<>();
		Move last = getLastKnownMove();
		if (last != null) {
			ListIterator<Move> it = gameStateExtension.moveIterator(gameState.getMrX(), true, last.getRoundNumber() + 1);
			while (it.hasNext()) {
				// hier koennts cast exception geben, bei corrupt game state
				list.add((Ticket) it.next().getItem()); 
			}
		}
		return list;
	}
	
}
