package kj.scotlyard.game.model;

import java.util.Set;

import kj.scotlyard.game.model.items.Item;

public interface Game extends GameState {
	
	void setMrX(MrXPlayer player);
	
	void setItems(Player player, Set<Item> items);
	
	
	void setCurrentRoundNumber(int roundNumber);
	
	void setCurrentPlayer(Player player);
	
}
