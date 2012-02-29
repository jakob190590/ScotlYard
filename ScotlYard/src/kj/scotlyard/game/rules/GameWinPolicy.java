package kj.scotlyard.game.rules;

import kj.scotlyard.game.GameState;
import kj.scotlyard.game.Move;

public interface GameWinPolicy {

	GameWin isGameWinningMove(GameState game, Move move);
	
	GameWin isGameWon(GameState game);
	
}
