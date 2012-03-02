package kj.scotlyard.game.rules;

import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.PlayingPiece;

public interface TurnPolicy {

	PlayingPiece getNextPiece(GameState gameState);
	
}
