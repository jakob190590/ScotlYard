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


public interface GameState {
	
	enum MoveAccessMode {
        ROUND_NUMBER,
        MOVE_NUMBER
    }
	
	int INITIAL_ROUND_NUMBER = 0;
	
	int INITIAL_MOVE_NUMBER = 0;
	
	int LAST_ROUND_NUMBER = -1;
	
	int LAST_MOVE_NUMBER = -1;
	
	/** Speziell fuer <tt>getMoves().remove/get(LAST_MOVE)</tt>. */
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
	
	List<Player> getPlayers();
	
	Set<Item> getItems(Player player);
	
	
	List<Move> getMoves();
	
	Move getMove(Player player, int number, MoveAccessMode accessMode);
	
	Move getLastMove(Player player);
	
	
	int getCurrentRoundNumber();
	
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
