package kj.scotlyard.game.util;

import java.util.HashSet;
import java.util.Set;

import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.Item;

public class ItemDealer {
	
	private Game game;

	public ItemDealer(Game game) {
		this.game = game;
	}
	
	
	
	public Set<Item> getItems(Player player) {
		return game.getItems(player);
	}
	
	public void setItems(Player player, Set<Item> items) {
		game.setItems(player, items);
	}
	
	
	
	int countItems(Player player, Class<? extends Item> itemType) {
		int n = 0;
		for (Item i : game.getItems(player))
			if (i.getClass() == itemType)
				n++;

		return n;
	}

	void addItems(Player player, int count, Class<? extends Item> itemType) {
		try {
			
			Set<Item> set = new HashSet<>();
			for (int i = 0; i < count; i++) {
				set.add(itemType.newInstance());
			}

			game.getItems(player).addAll(set);

		} catch (Exception e) {
			throw new IllegalArgumentException("Does not work for given item class. " +
					"You can create the items manually and add them via getItems.", e);
		}
	}
	
	int passItems(Player fromPlayer, Player toPlayer, int count, Class<? extends Item> itemType) {
		int n = 0;
		for (Item i : game.getItems(fromPlayer))		
			if (n < count) {
				if (i.getClass() == itemType) {
					game.getItems(fromPlayer).remove(i);
					game.getItems(toPlayer).add(i);
					n++;
				}
			} else {
				break;
			}

		return n;
	}
	
	int removeItems(Player player, int count, Class<? extends Item> itemType) {
		int n = 0;
		for (Item i : game.getItems(player))		
			if (n < count) {
				if (i.getClass() == itemType) {
					game.getItems(player).remove(i);
					n++;
				}
			} else {
				break;
			}

		return n;
	}
	
	int removeAllItems(Player player, Class<? extends Item> itemType) {
		int n = 0;
		for (Item i : game.getItems(player))		
			if (i.getClass() == itemType) {
				game.getItems(player).remove(i);
				n++;
			}
		return n;
	}

}
