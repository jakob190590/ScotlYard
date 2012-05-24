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

package kj.scotlyard.game.rules;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.Ticket;

public interface MovePolicy {	
	
	boolean canMove(GameState gameState, GameGraph gameGraph, Player player);
	
	boolean isTicketValidForConnection(Ticket ticket, ConnectionEdge connection);
	
	void checkMove(GameState gameState, GameGraph gameGraph, Move move) throws IllegalMoveException;
	
	Player getNextItemOwner(GameState gameState, Move move, Item item); // Parameter Item, falls es mal mehrere Items in einem Move gibt.
	
}
