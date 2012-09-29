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

package kj.scotlyard.board;

import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;

public class MovePreparationEvent {
	
	// Event IDs TODO javadoc
	/**
	 * If a player is selected. The field
	 * move shall be <code>null</code>.
	 */
	public static final int SELECT_PLAYER = 0;
	
	/**
	 * If next station is stated.
	 */
	public static final int NEXT_STATION = 1;
	
	/**
	 * If a prepared move is reseted.
	 */
	public static final int RESET = 2;
	
	/**
	 * If all prepared moves are reseted. The
	 * field player must be <code>null</code>.
	 */
	public static final int RESET_ALL = 3;
	
	/** Event ID - gibt den Typ an */
	private int id;
	
	private Player player;
	
	private Move move;

	public MovePreparationEvent(int id, Player player, Move move) {
		this.id = id;
		this.player = player;
		this.move = move;
	}

	public int getId() {
		return id;
	}

	public Player getPlayer() {
		return player;
	}

	public Move getMove() {
		return move;
	}

}
