package kj.scotlyard.game.model;

import java.util.Set;

import kj.scotlyard.game.model.items.Item;

public interface ItemListener {

	void itemAdded(GameState gameState, Player player, Item item);
	
	void itemRemoved(GameState gameState, Player player, Item item);
	
	void itemSetChanged(GameState gameState, Player player, Set<Item> oldItems, Set<Item> newItems);
	
}
