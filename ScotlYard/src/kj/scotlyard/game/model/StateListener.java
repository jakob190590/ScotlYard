package kj.scotlyard.game.model;

public interface StateListener {

	void currentRoundChanged(GameState gameState, int previousRoundNumber, int nextRoundNumber);
	
}
