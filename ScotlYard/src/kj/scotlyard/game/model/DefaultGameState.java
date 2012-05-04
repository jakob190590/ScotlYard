package kj.scotlyard.game.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import kj.scotlyard.game.model.item.Item;

public class DefaultGameState extends AbstractGameState {
	
	private GameState gameState;
	
	private List<DetectivePlayer> detectives;
	
	private List<Player> players;
	
	private List<Move> moves;
	

	public DefaultGameState(GameState gameState) {		
		this.gameState = gameState;
		
		detectives = Collections.unmodifiableList(gameState.getDetectives());
		players = Collections.unmodifiableList(gameState.getPlayers());
		moves = Collections.unmodifiableList(gameState.getMoves());
		
		// This new listeners have to inform our own listeners
		// and pass this GameState as argument!
		gameState.addTurnListener(getTurnListenerInformer());
		gameState.addPlayerListener(getPlayerListenerInformer());
		gameState.addItemListener(getItemListenerInformer());
		gameState.addMoveListener(getMoveListenerInformer());
	}
	
	/** 
	 * Getter, weil abgeleitete Klassen u.a.
	 * in copy darauf zugreifen muessen.
	 */
	protected GameState getGameState() {
		return gameState;
	}

	@Override
	public DefaultGameState copy() {
		return new DefaultGameState(gameState.copy());
	}

	@Override
	public MrXPlayer getMrX() {
		return gameState.getMrX();
	}

	@Override
	public List<DetectivePlayer> getDetectives() {
		return detectives;
	}

	@Override
	public List<Player> getPlayers() {
		return players;
	}

	@Override
	public Set<Item> getItems(Player player) {
		return Collections.unmodifiableSet(gameState.getItems(player));
	}

	@Override
	public List<Move> getMoves() {
		return moves;
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
