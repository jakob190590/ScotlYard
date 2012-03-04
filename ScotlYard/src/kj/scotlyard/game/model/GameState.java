package kj.scotlyard.game.model;

import java.util.List;
import java.util.Set;

import kj.scotlyard.game.items.Item;


public interface GameState {
	
	enum MoveAccessMode {
        ROUND_NUMBER,
        MOVE_NUMBER
    }
	
	int INITIAL_ROUND_NUMBER = 0;
	
	MrXPlayer getMrX();
	
	List<DetectivePlayer> getDetectives();
	
	List<Player> getPlayingPieces();
	
	Set<Item> getItems(Player piece);
	
	
	List<Move> getMoves();
	
	Move getMove(Player piece, int number, MoveAccessMode accessMode);
	
	
	int getCurrentRoundNumber();
	
	Player getCurrentPlayingPiece();
	
	
	// To add and remove Listeners

	
	
}
