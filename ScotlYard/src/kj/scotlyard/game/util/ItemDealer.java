/*
 * ScotlYard -- A software implementation of the Scotland Yard board game
 * Copyright (C) 2012  Jakob Schöttl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package kj.scotlyard.game.util;

import java.util.HashSet;
import java.util.Iterator;
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
		Iterator<Item> it = items1.iterator();
		while (it.hasNext()) {
			Item i = it.next();	
			if (n < count) {
				if (i.getClass() == itemType) {
					it.remove();
					items2.add(i);
					n++;
				}
			} else {
				break;
			}
		}

		return n;
	}
	
	public int removeItems(Player player, int count, Class<? extends Item> itemType) {
		int n = 0;
		Iterator<Item> it = game.getItems(player).iterator();
		while (it.hasNext()) {
			Item i = it.next();
			if (n < count) {
				if (i.getClass() == itemType) {
					it.remove();
					n++;
				}
			} else {
				break;
			}
		}

		return n;
	}
	
	public int removeAllItems(Player player, Class<? extends Item> itemType) {
		int n = 0;
		Iterator<Item> it =  game.getItems(player).iterator();
		while (it.hasNext()) {
			Item i = it.next();		
			if (i.getClass() == itemType) {
				it.remove();
				n++;
			}
		}
		return n;
	}

}
