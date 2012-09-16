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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.item.Ticket;

/**
 * Builder interface for a Sub Move.
 * An instance should be passed to MoveProducer's 
 * method createMultiMove as parameter.
 * 
 * This builder is designed for simple use like this:
 * <tt>SubMoves sm = new SubMoves().add(...).add(...);</tt>
 * 
 * @author jakob190590
 *
 */
public class SubMoves implements Iterable<Move> {
	
	private List<Move> subMoves = new LinkedList<>();
	
	public SubMoves add(StationVertex station, ConnectionEdge connection, Ticket ticket) {
		
		// Player and Numbers will be set later in createMultiMove
		subMoves.add(new DefaultMove(null, 0, Move.NO_MOVE_NUMBER, 
				Move.NO_MOVE_INDEX, station, connection, ticket));
				
		return this;
	}

	@Override
	public Iterator<Move> iterator() {		
		return subMoves.iterator();
	}

}
