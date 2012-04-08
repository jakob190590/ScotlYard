package kj.scotlyard.game.rules;

import java.util.List;

import kj.scotlyard.game.model.GameState;

public interface GameStateAccessPolicy {
	
	List<Integer> getMrXUncoverMoveNumbers();
	
	GameState createGameStateForDetectives(GameState gameState);

}
