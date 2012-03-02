package kj.scotlyard.game.rules;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;

public interface MovePolicy {	
	
	void checkMove(GameState gameState, GameGraph gameGraph, Move move) throws IllegalMoveException;
	
}
