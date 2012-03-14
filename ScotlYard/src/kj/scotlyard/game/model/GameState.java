package kj.scotlyard.game.model;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import kj.scotlyard.game.model.items.Item;


public interface GameState {
	
	enum MoveAccessMode {
        ROUND_NUMBER,
        MOVE_NUMBER
    }
	
	int INITIAL_ROUND_NUMBER = 0;
	
	int INITIAL_MOVE_NUMBER = 0;
	
	MrXPlayer getMrX();
	
	List<DetectivePlayer> getDetectives();
	
	List<Player> getPlayers();
	
	Set<Item> getItems(Player player);
	
	
	Stack<Move> getMoves();
	
	Move getMove(Player player, int number, MoveAccessMode accessMode);
	
	Move getLastMove(Player player);
	
	
	int getCurrentRoundNumber();
	
	Player getCurrentPlayer();
	
	
	// To add and remove Listeners

	
	
}
