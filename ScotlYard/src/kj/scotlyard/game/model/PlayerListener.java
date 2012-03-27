package kj.scotlyard.game.model;

public interface PlayerListener {
	
	void detectiveAdded(GameState gameState, DetectivePlayer detective, int atIndex);
	
	void detectiveRemoved(GameState gameState, DetectivePlayer detective, int atIndex);
	
	void mrXSet(GameState gameState, MrXPlayer oldMrX, MrXPlayer newMrX);
	
}
