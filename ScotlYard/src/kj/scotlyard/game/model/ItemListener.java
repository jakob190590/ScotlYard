package kj.scotlyard.game.model;

import kj.scotlyard.game.items.Item;

public interface ItemListener {

	void itemAdded(GameState gameState, Player player, Item item);
	
	void itemRemoved(GameState gameState, Player player, Item item);
	
	void itemSetChanged(GameState gameState, Player player);
	
}
