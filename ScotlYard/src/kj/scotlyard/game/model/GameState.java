package kj.scotlyard.game.model;

import java.util.List;
import java.util.Set;

import kj.scotlyard.game.model.items.Item;


public interface GameState {
	
	enum MoveAccessMode {
        ROUND_NUMBER,
        MOVE_NUMBER
    }
	
	int INITIAL_ROUND_NUMBER = 0;
	
	int INITIAL_MOVE_NUMBER = 0;
	
	int LAST_ROUND_NUMBER = -1;
	
	int LAST_MOVE_NUMBER = -1;
	
	/** Speziell fuer <tt>getMoves().remove(LAST_MOVE)</tt>. */
	int LAST_MOVE = -1;
	
	MrXPlayer getMrX();
	
	List<DetectivePlayer> getDetectives();
	
	List<Player> getPlayers();
	
	Set<Item> getItems(Player player);
	
	
	List<Move> getMoves();
	
	Move getMove(Player player, int number, MoveAccessMode accessMode);
	
	Move getLastMove(Player player);
	
	Move getLastMove();
	
	
	int getCurrentRoundNumber();
	
	Player getCurrentPlayer();
	
	
	// To add and remove Listeners ...

	void addStateListener(StateListener listener);
	
	void removeStateListener(StateListener listener);
	
	void addPlayerListener(PlayerListener listener);
	
	void removePlayerListener(PlayerListener listener);
	
	void addItemListener(ItemListener listener);
	
	void removeItemListener(ItemListener listener);
	
	void addMoveListener(MoveListener listener);
	
	void removeMoveListener(MoveListener listener);
	
}
