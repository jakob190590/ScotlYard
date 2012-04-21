package kj.scotlyard.game.rules;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.GameState;

public interface TurnPolicy {

	Turn getNextTurn(GameState gameState, GameGraph gameGraph);

}
