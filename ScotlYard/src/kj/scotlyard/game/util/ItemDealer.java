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
	
	
	
	public int countItems(Player player, Class<? extends Item> itemType) {
		int n = 0;
		for (Item i : game.getItems(player))
			if (i.getClass() == itemType)
				n++;

		return n;
	}

	public void addItems(Player player, int count, Class<? extends Item> itemType) {
		
		Set<Item> set = new HashSet<>();
		try {			
			for (int i = 0; i < count; i++) {
				set.add(itemType.newInstance());
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Does not work for given item class. " +
					"You can create the items manually and add them via getItems.", e);
		}
		
		game.getItems(player).addAll(set);
	}
	
	public int passItems(Player fromPlayer, Player toPlayer, int count, Class<? extends Item> itemType) {
		Set<Item> items1 = game.getItems(fromPlayer);
		Set<Item> items2 = game.getItems(toPlayer);
		int n = 0;
		for (Item i : items1)		
			if (n < count) {
				if (i.getClass() == itemType) {
					items1.remove(i);
					items2.add(i);
					n++;
				}
			} else {
				break;
			}

		return n;
	}
	
	public int removeItems(Player player, int count, Class<? extends Item> itemType) {
		Set<Item> items = game.getItems(player);
		int n = 0;
		for (Item i : items)		
			if (n < count) {
				if (i.getClass() == itemType) {
					items.remove(i);
					n++;
				}
			} else {
				break;
			}

		return n;
	}
	
	public int removeAllItems(Player player, Class<? extends Item> itemType) {
		Set<Item> items = game.getItems(player);
		int n = 0;
		for (Item i : items)		
			if (i.getClass() == itemType) {
				items.remove(i);
				n++;
			}
		return n;
	}

}
