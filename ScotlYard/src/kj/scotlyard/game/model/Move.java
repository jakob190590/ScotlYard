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

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.item.Item;

import org.apache.commons.attributes.Sealable;

/**
 * This interface defines a (hierarchical) move in a game. Once a move is added
 * to the move list of a <code>Game</code>, it is sealed automatically.
 * 
 * There are names for different kind of moves:
 * <ul>
 * <li>Single Move - a move <code>m</code> meeting the condition
 * <code>m.getMoves().isEmpty()</code> (that is no Sub Moves).</li>
 * <li>Multi Move - a not-Single-Move including its Sub Move(s).</li>
 * <li>Sub Move - a move <code>m</code> meeting the condition
 * <code>aBaseMove.getMoves().contains(m)</code>.</li>
 * <li>Base Move - just the root move in a Multi Move hierarchy.</li>
 * </ul>
 * 
 * @author jakob190590
 * 
 */
public interface Move extends Sealable {
	
	// Round Number gibts immer
	
	/**
	 * Only Single Moves have a move number. This constant is for non-Single-Moves.
	 */
	int NO_MOVE_NUMBER = -1;
	
	/**
	 * Constant for the move index in a non-Sub-Move.
	 */
	int NO_MOVE_INDEX = -1;
	
	Player getPlayer();

	void setPlayer(Player player);

	int getRoundNumber();

	/**
	 * The round number of the move. This must be set for every kind of move.
	 * @param roundNumber
	 */
	void setRoundNumber(int roundNumber);

	int getMoveNumber();
	
	/**
	 * Set the move number for a Single Move. For the Base Move of a Multi Move
	 * this should be <code>NO_MOVE_NUMBER</code>.
	 * 
	 * @param moveNumber
	 */
	void setMoveNumber(int moveNumber);
	
	int getMoveIndex();
	
	/**
	 * Set the move index for Sub Moves within a Multi Move. For a
	 * non-Sub-Move this should be <code>NO_MOVE_INDEX</code>.
	 * The move index is for identifying and ordering Sub Moves.
	 * 
	 * @param moveIndex
	 */
	void setMoveIndex(int moveIndex);
	

	StationVertex getStation();

	/**
	 * Set the destination station of a move. For the Base Move of a Multi Move,
	 * this is the very last station.
	 * 
	 * @param station
	 */
	void setStation(StationVertex station);
	
	ConnectionEdge getConnection();
	
	/**
	 * Set the connection used to approach the destination station
	 * (<code>getStation()</code>). This is only defined for Single Moves.
	 * 
	 * @param connection
	 */
	void setConnection(ConnectionEdge connection);


	// List/Set<Item> getItems();
	// oder
	Item getItem();
	
	/**
	 * Set the <code>Item</code> used in this move. In a Single Move this is
	 * usually a <code>Ticket</code>, in the Base Move of a Multi Move this is
	 * usually a MultiMoveCard.
	 * 
	 * @param item
	 */
	void setItem(Item item);
	
	/**
	 * A list of all (direct) Sub Moves of this move.
	 * If the list is not empty, this move is called
	 * the Base Move.
	 * @return
	 */
	List<Move> getMoves();

}
