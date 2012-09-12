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

import java.util.Set;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.rules.MovePolicy;
import kj.scotlyard.game.rules.TheMovePolicy;

public class MoveHelper {
	
	private static final MovePolicy movePolicy = new TheMovePolicy();
	
	/**
	 * Returns the next best valid Ticket from items for the given
	 * connection, or <tt>null</tt> if there is no such Ticket.
	 * @param connection the connection for which a Ticket is requested
	 * @param items the Set of Items, within which the search is conducted
	 * @return a valid Ticket for connection, or <tt>null</tt> if there is no such Ticket
	 */
	public static Ticket anyTicket(ConnectionEdge connection, Set<Item> items) {
		for (Item i : items) {
			if (i instanceof Ticket) {
				Ticket t = (Ticket) i;
				if (movePolicy.isTicketValidForConnection(t, connection)) {
					return t;
				}
			}
		}
		return null;
	}
	
	/**
	 * Schlaegt eine Verbindung zwischen a und b vor, fuer die das Ticket
	 * gueltig ist. Die Methode kann von der GUI benutzt werden, weil dem
	 * Spieler ja egal ist, welche Verbindung er genau nutzt -- hauptsache
	 * er kommt von a nach b und der Zug ist gueltig.
	 * @param a the source station
	 * @param b the target station
	 * @param ticket the Ticket, which should be used
	 * @return the next best connection between two stations,
	 * for which the given Ticket is valid, or <tt>null</tt>
	 * if there is no such connection
	 */
	public static ConnectionEdge suggestConnection(StationVertex a, StationVertex b, Ticket ticket) {
		for (ConnectionEdge c : a.getEdges()) {
			StationVertex s = c.getOther(a);
			if (s == b && movePolicy.isTicketValidForConnection(ticket, c)) {
				return c;
			}
		}
		return null;
	}
}
