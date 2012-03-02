package kj.scotlyard.game.rules;

import java.util.List;

import kj.scotlyard.game.model.GameState;

public interface GameStateAccessPolicy {
	
	GameState createGameStateForDetectives(GameState gameState); // get... passt nicht, weil man dann davon ausgeht, dass es immer der gleiche ist. es sollte aber auch klar sein, dass das keine factory method is.
	
	List<Integer> getMrXUncoverMoveNumbers();

}
