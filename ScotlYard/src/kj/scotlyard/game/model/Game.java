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

package kj.scotlyard.game.model;

import java.util.Set;

import kj.scotlyard.game.model.item.Item;

/**
 * The Game interface extends the GameState, to 
 * allow modifying state, players, move history
 * and items.
 * @author jakob190590
 *
 */
public interface Game extends GameState {
	
	void setMrX(MrXPlayer player);
	
	/**
	 * Set an item set for the specified player.
	 * For each player the item set must be attached
	 * manually with this method.
	 * @param player
	 * @param items
	 */
	void setItems(Player player, Set<Item> items);
	
	/**
	 * Set the current round number. The number must be 
	 * greater than or equal to 
	 * <code>GameState.INITIAL_ROUND_NUMBER</code>.
	 * @param roundNumber
	 */
	void setCurrentRoundNumber(int roundNumber);
	
	/**
	 * Set the player whose turn it is. The parameter
	 * can be <code>null</code> to indicate, that it's
	 * no player's turn (when the game has not started
	 * or has ended). 
	 * @param player
	 */
	void setCurrentPlayer(Player player);
	
}
