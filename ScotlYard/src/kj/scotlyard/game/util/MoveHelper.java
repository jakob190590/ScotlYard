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

import java.util.List;
import java.util.Set;

import org.jgrapht.alg.BellmanFordShortestPath;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.rules.MovePolicy;
import kj.scotlyard.game.rules.TheMovePolicy;

public class MoveHelper {
	
	private static final MovePolicy movePolicy = new TheMovePolicy();
	
	/**
	 * Returns the next best valid Ticket from items for the given
	 * connection, or <tt>null</tt> if there is no such Ticket.
	 * This method is only for testing purpose e.g. <code>MoveProducer
	 * .nextBestSingleMove()</code> use it.
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
	
	/**
	 * Liefert den einzigen Spieler, der auf die angegebene Station fahren kann. 
	 * Dabei werden keine Regeln beachtet, sondern nur geprüft, ob der Abstand im
	 * Graph genau eins ist!
	 * Wenn die Situation nicht eindeutig ist, ist das Ergebnis <code>null</code>.
	 * Die Situation ist nicht eindeutig, wenn andere Spieler zu nahe an der
	 * Station sind (d.h. der Abstand im Graph zu gering ist).
	 * 
	 * Algorithmus: Eindeutig, welcher Spieler gemeint ist, wenn
	 * <ol>
	 * <li>Station nur durch ihn erreichbar ist (Distanz == 1)</li>
	 * <li>Distanz zwischen Station und allen anderen Spielern größer als N mit N >= 1 (Distanz > N)</li>
	 * </ol>
	 * @param gameState
	 * @param gameGraph
	 * @param station Station, zu der wir einen eindeutigen Spieler suchen
	 * @return unambigous player or <code>null</code>
	 */
	public static Player unambiguousPlayer(GameState gameState, GameGraph gameGraph, StationVertex station) {
		// Bis einschliesslich N zaehlen Distanzen als "gering" (smallDistance)
		final int N = 1; // N >= 1
		
		Player player = null;
		int smallDistance = 0; // Zaehler fuer Faelle, in denen ein Player eine geringe Distanz zu station hat
		for (Player p : gameState.getPlayers()) {
			int d = BellmanFordShortestPath.findPathBetween(gameGraph.getGraph(), 
					station, gameState.getLastMove(p).getStation()).size();
			if (d <= N) {
				// Geringe Distanz
				smallDistance++;
				if (d == 1) {
					player = p;
				}
			}
		}
		
		// Mehr als ein Player mit geringer Distanz zu station
		if (smallDistance > 1) {
			// nicht eindeutig
			return null;
		}
		
		return player;
	}
	
	/**
	 * Liefert den einzigen Detektiv, der auf die angegebene Station fahren kann. 
	 * Dabei werden keine Regeln beachtet, sondern nur geprüft, ob der Abstand im
	 * Graph genau eins ist!
	 * Wenn die Situation nicht eindeutig ist, ist das Ergebnis <code>null</code>.
	 * Die Situation ist nicht eindeutig, wenn andere Detektive zu nahe an der
	 * Station sind (d.h. der Abstand im Graph zu gering ist).
	 * 
	 * Algorithmus: Eindeutig, welcher Detektiv gemeint ist, wenn
	 * <ol>
	 * <li>Station nur durch ihn erreichbar ist (Distanz == 1)</li>
	 * <li>Distanz zwischen Station und allen anderen Spielern größer als N mit N >= 1 (Distanz > N)</li>
	 * </ol>
	 * @param gameState
	 * @param gameGraph
	 * @param station Station, zu der wir einen eindeutigen Spieler suchen
	 * @return unambigous player or <code>null</code>
	 */
	public static DetectivePlayer unambiguousDetective(GameState gameState, GameGraph gameGraph, StationVertex station) {
		// Bis einschliesslich N zaehlen Distanzen als "gering" (smallDistance)
		final int N = 1; // N >= 1
		
		DetectivePlayer player = null;
		int smallDistance = 0; // Zaehler fuer Faelle, in denen ein Detective eine geringe Distanz zu station hat
		for (DetectivePlayer p : gameState.getDetectives()) {
			int d = BellmanFordShortestPath.findPathBetween(gameGraph.getGraph(), 
					station, gameState.getLastMove(p).getStation()).size();
			if (d <= N) {
				// Geringe Distanz
				smallDistance++;
				if (d == 1) {
					player = p;
				}
			}
		}
		
		// Mehr als ein Player mit geringer Distanz zu station
		if (smallDistance > 1) {
			// nicht eindeutig
			return null;
		}
		
		return player;
	}
	
	public static Player unambiguousPlayer(GameState gameState, GameGraph gameGraph, StationVertex station, List<Player> players) {
		// TODO Vllt waere das sinnvoll, dann kann auf die relevanten Player eingegraenzt werden!
		return null;
	}
	
	public static Set<Player> getPlayersNearby(GameState gameState, GameGraph gameGraph, StationVertex station) {
		// TODO Vllt waere das sinnvoll, weil dann player die schon dran waren aussortiert werden koennen.
		return null;
	}
}
