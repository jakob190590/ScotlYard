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

import java.util.List;
import java.util.Set;

import kj.scotlyard.game.model.item.Item;

/**
 * The GameState is an (read-only) access interface to 
 * the complete state and move history of the game.
 * @author jakob190590
 *
 */
public interface GameState {
	
	/** 
	 * Access mode for moves in the list. Round and
	 * move number are the same unless there are multi
	 * moves. A multi move has one round number, but 
	 * it's sub moves have different move numbers.
	 */
	enum MoveAccessMode {
		ROUND_NUMBER,
		MOVE_NUMBER
	}
	
	int INITIAL_ROUND_NUMBER = 0;
	
	int INITIAL_MOVE_NUMBER = 0;
	
	int LAST_ROUND_NUMBER = -1;
	
	int LAST_MOVE_NUMBER = -1;
	
	/** Speziell fuer <code>getMoves().remove/get(LAST_MOVE)</code>. */
	int LAST_MOVE = -1;
	
	
	/**
	 * Liefert eine Kopie des GameStates. Ich verzichte auf
	 * Cloneable und clone(), weil ich nicht ueberall diese
	 * verdammte checked CloneNotSupported Exception abfangen
	 * will.
	 * @return Kopie dieses GameStates
	 */
	GameState copy();
	
	
	MrXPlayer getMrX();
	
	List<DetectivePlayer> getDetectives();
	
	/**
	 * Returns an unmodifiable List with all players.
	 * If MrX is not <code>null</code>, it is the first
	 * player in the list, otherwise it is not in the list.
	 * This is followed by the detectives in the same order 
	 * as in the detective list, see <code>getDetectives()</code>.
	 * @return an unmodifiable list with all players
	 */
	List<Player> getPlayers();
	
	/**
	 * Returns the item set for the specified player or
	 * <code>null</code> if there is no associated set.
	 * @param player
	 * @return the item set or <code>null</code>
	 */
	Set<Item> getItems(Player player);
	
	
	/**
	 * Returns a list of all moves that have been made (move history).
	 * Note that negative indices mean that the move list is accessed 
	 * from it's end. That is -1 is the last move, -2 the last but one 
	 * and so on.
	 * @return a list of all moves
	 */
	List<Move> getMoves();
	
	/**
	 * Returns a specific move from the move history according to
	 * the parameter values. If number is negative, the move list 
	 * is accessed from it's end. That is -1 is the last move, -2
	 * the last but one and so on.
	 * @param player
	 * @param number the move or round number (depending on accessMode)
	 * @param accessMode the access mode for moves
	 * @return the specified move
	 */
	Move getMove(Player player, int number, MoveAccessMode accessMode);
	
	/**
	 * Returns the last move of the player or <code>null</code> if
	 * the player has not moved yet.
	 * @param player
	 * @return the last move or <code>null</code>
	 */
	Move getLastMove(Player player);
	
	/**
	 * Returns the current round number. The round 
	 * number must be greater than or equal to
	 * <code>INITIAL_ROUND_NUMBER</code>.
	 * @return the current round number
	 */
	int getCurrentRoundNumber();
	
	/**
	 * Returns the player whose turn it is or <code>null</code>
	 * if the game has not started or has ended.
	 * @return the current player or <code>null</code>
	 */
	Player getCurrentPlayer();
	
	
	// To add and remove Listeners ...

	void addTurnListener(TurnListener listener);
	
	void removeTurnListener(TurnListener listener);
	
	void addPlayerListener(PlayerListener listener);
	
	void removePlayerListener(PlayerListener listener);
	
	void addItemListener(ItemListener listener);
	
	void removeItemListener(ItemListener listener);
	
	void addMoveListener(MoveListener listener);
	
	void removeMoveListener(MoveListener listener);
	
}
