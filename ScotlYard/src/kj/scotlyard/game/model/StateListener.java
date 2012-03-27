package kj.scotlyard.game.model;

public interface StateListener {

	void currentPlayerChanged(GameState gameState, Player oldPlayer, Player newPlayer);
	
	void currentRoundChanged(GameState gameState, int oldRoundNumber, int newRoundNumber);
	
}
