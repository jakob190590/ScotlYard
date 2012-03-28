package kj.scotlyard.game.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import kj.scotlyard.game.model.items.Item;

public class FullGameState extends AbstractGameState {
	
	private GameState gameState;

	public FullGameState(GameState gameState) {		
		this.gameState = gameState;
		
		// This new listeners have to inform our own listeners
		// and pass this GameState as argument!
		gameState.addStateListener(getStateListenerInformer());
		gameState.addPlayerListener(getPlayerListenerInformer());
		gameState.addItemListener(getItemListenerInformer());
		gameState.addMoveListener(getMoveListenerInformer());
	}

	@Override
	public MrXPlayer getMrX() {
		return gameState.getMrX();
	}

	@Override
	public List<DetectivePlayer> getDetectives() {
		return Collections.unmodifiableList(gameState.getDetectives());
	}

	@Override
	public List<Player> getPlayers() {
		return gameState.getPlayers();
	}

	@Override
	public Set<Item> getItems(Player player) {
		return Collections.unmodifiableSet(gameState.getItems(player));
	}

	@Override
	public List<Move> getMoves() {
		return Collections.unmodifiableList(gameState.getMoves());
	}

	@Override
	public Move getMove(Player player, int number, MoveAccessMode accessMode) {
		return gameState.getMove(player, number, accessMode);
	}

	@Override
	public Move getLastMove(Player player) {
		return gameState.getLastMove(player);
	}

	@Override
	public int getCurrentRoundNumber() {
		return gameState.getCurrentRoundNumber();
	}

	@Override
	public Player getCurrentPlayer() {
		return gameState.getCurrentPlayer();
	}

}
