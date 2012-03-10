package kj.scotlyard.game.model;

public interface MoveListener {

	void moveDone(GameState gameState, Move move);
	
	void moveUndone(GameState gameState, Move move);
	
}
