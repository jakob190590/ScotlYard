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

import org.apache.commons.attributes.Sealable;

import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;

public interface Move extends Sealable {
	
	// Round Number gibts immer
	
	int NO_MOVE_NUMBER = -1;
	
	int NO_MOVE_INDEX = -1;
	
	Player getPlayer();

	void setPlayer(Player player);

	int getRoundNumber();

	void setRoundNumber(int roundNumber);

	int getMoveNumber();
	
	void setMoveNumber(int moveNumber);
	
	int getMoveIndex();
	
	void setMoveIndex(int moveIndex);
	

	StationVertex getStation();

	void setStation(StationVertex station);
	
	ConnectionEdge getConnection();
	
	void setConnection(ConnectionEdge connection);


	// List/Set<Item> getItems();
	// oder
	Item getItem();
	
	void setItem(Item item);
	
	List<Move> getMoves();

}
