package kj.scotlyard.game.rules;

import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Player;

public interface TurnPolicy {

	Player getNextPlayer(GameState gameState);
	
	int getNextRoundNumber(GameState gameState);
	
}
