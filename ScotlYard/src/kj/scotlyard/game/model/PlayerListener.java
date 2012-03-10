package kj.scotlyard.game.model;

public interface PlayerListener {

	void currentPlayerChanged(GameState gameState, Player previousPlayer, Player nextPlayer);
	
	void detectiveAdded(GameState gameState, DetectivePlayer detective);
	
	void detectiveRemoved(GameState gameState, DetectivePlayer detective);
	
	void mrXSet(GameState gameState, MrXPlayer mrX);
	
}
