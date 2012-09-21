package kj.scotlyard.board;

import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.PlayerListener;

public abstract class PlayerListenerAdapter implements PlayerListener {
	
	public abstract void playerListChanged(GameState gameState);

	@Override
	public void detectiveAdded(GameState gameState, DetectivePlayer detective,
			int atIndex) {
		playerListChanged(gameState);
	}

	@Override
	public void detectiveRemoved(GameState gameState,
			DetectivePlayer detective, int atIndex) {
		playerListChanged(gameState);
	}

	@Override
	public void mrXSet(GameState gameState, MrXPlayer oldMrX, MrXPlayer newMrX) {
		playerListChanged(gameState);
	}

}
