package kj.scotlyard.game.rules;

import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;

public interface GameWinPolicy {

	GameWin isGameWinningMove(GameState gameState, Move move);
	
	GameWin isGameWon(GameState gameState);
	
}
