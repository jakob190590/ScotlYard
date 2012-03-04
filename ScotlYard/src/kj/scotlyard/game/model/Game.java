package kj.scotlyard.game.model;

import java.util.Set;

import kj.scotlyard.game.items.Item;

public interface Game extends GameState {
	
	void setMrX(MrXPlayer piece);
	
	void setItems(Player piece, Set<Item> items);
	
	
	void setCurrentRoundNumber(int roundNumber);
	
	void setCurrentPlayingPiece(Player piece);
	
}
