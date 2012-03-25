package kj.scotlyard.game.rules;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.GameState;

public interface GameWinPolicy {

	GameWin isGameWon(GameState gameState, GameGraph gameGraph);
	
}
