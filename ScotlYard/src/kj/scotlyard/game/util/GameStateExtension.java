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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.Item;

public class GameStateExtension {
	
	private class MoveIterator implements ListIterator<Move> {

		private ListIterator<Move> it;
		
		private Player player;
		
		private Move next;
		
		private Move previous;
		
		private boolean wentForward;
		
		private boolean wentBackward;
		
		public MoveIterator(List<Move> list, Player player) {
			
			this.player = player;
			
			it = list.listIterator();
			
			while (it.hasNext()) {
				Move m = it.next();
				if (m.getPlayer() == player) {
					next = m;
					break;
				}
			}
		}
		
		public MoveIterator(List<Move> list, Player player, boolean flat, int moveRespRoundNumber) {
			
			this(list, player);
					
			boolean found = false;
			while (this.hasNext()) {
				Move m = this.next();
				if ((flat && m.getMoveNumber() == moveRespRoundNumber) 
						|| !flat && m.getRoundNumber() == moveRespRoundNumber) {

					found = true;
					break;
				}
			}
			
			if (found) {
				// Wieder eins zurueck, weil wir diesen Move nicht uebergehen wollen
				if (this.hasPrevious()) {
					this.previous();
				}
			}
		}
		
		private void throwIndexNotSupportedException() {
			throw new UnsupportedOperationException("This method makes no sense for the MoveIterator: " +
					"The actual list index is useless, because you don't know to wich list it refers -- " +
					"And returning the move resp. round number would be contrary to the method name.");
		}
		
		private void throwModificationNotSupportedException() {
			throw new UnsupportedOperationException("Modification not supported.");
		}

		// Algorithmus ist so, dass it immer eins vorraus ist. Immer beim
		//  Wechsel next/prev muss folglich ein Element uebergangen werden.
		
		@Override
		public boolean hasNext() {			
			return (next != null);
		}

		@Override
		public Move next() {
			if (next == null) {
				throw new NoSuchElementException("There is no next element.");
			}
			
			// Rueckgabewert steht schon fest!
			Move result = next;			
			
			// bei richtungswechsel curser richtig positionieren
			if (wentBackward) {
				wentBackward = false;
				while (it.hasNext() && it.next() != result);
			}
			
			previous = result;
			next = null; // falls kein nachfolger gefunden wird
			
			// Vorraus den naechsten Nachfolger suchen
			while (it.hasNext()) {
				Move m = it.next();
				if (m.getPlayer() == player) {
					next = m;
					break;
				}
			}
			
			wentForward = true;
			return result;
		}

		@Override
		public boolean hasPrevious() {
			return (previous != null);
		}

		@Override
		public Move previous() {
			if (previous == null) {
				throw new NoSuchElementException("There is no previous element.");
			}
			
			// Rueckgabewert steht schon fest!
			Move result = previous;
			
			// bei richtungswechsel curser richtig positionieren
			if (wentForward) {
				wentForward = false;
				while (it.hasPrevious() && it.previous() != result);
			}
			
			next = result;
			previous = null; // falls kein vorgaenger gefunden wird
			
			// Vorraus den naechsten Vorgaenger suchen
			while (it.hasPrevious()) {
				Move m = it.previous();
				if (m.getPlayer() == player) {
					previous = m;
					break;
				}
			}
			
			wentBackward = true;
			return result;
		}

		@Override
		public int nextIndex() {
			throwIndexNotSupportedException();
			return 0;
		}

		@Override
		public int previousIndex() {
			throwIndexNotSupportedException();
			return 0;
		}

		@Override
		public void remove() {
			throwModificationNotSupportedException();
		}

		@Override
		public void set(Move e) {
			throwModificationNotSupportedException();
		}

		@Override
		public void add(Move e) {
			throwModificationNotSupportedException();
		}
		
	}
	
	private GameState gameState;
	
	public GameStateExtension(GameState gameState) {
		this.gameState = gameState;
	}

	/**
	 * Gibt eine flache Liste aller Moves zurueck. Bei Multi Moves werden
	 * nur alle Sub Moves eingetragen, nicht der Base Move.
	 * (Verwendet <tt>flattenMove(Move)</tt>.)
	 * @return Liste aller Moves
	 */
	public List<Move> getMovesFlat() {
		List<Move> result = new LinkedList<>();
		for (Move m : gameState.getMoves()) {
			// auch das hier koennte man effizienter machen:
			// if (m.getMoves().isEmpty()) result.add(m); else ... egal
			result.addAll(flattenMove(m));
		}
		return result;
	}
	
	public ListIterator<Move> moveIterator(Player player, boolean flat, int moveRespRoundNumber) {
		List<Move> all = (flat) ? getMovesFlat() : gameState.getMoves();
		
		ListIterator<Move> it;		
		if (moveRespRoundNumber < 0) {
			it = new MoveIterator(all, player);
		} else {
			it = new MoveIterator(all, player, flat, moveRespRoundNumber);
		}
		return it;
	}
	
	public ListIterator<Move> moveIterator(Player player, boolean flat) {
		return moveIterator(player, flat, -1);
	}
	
	public List<Move> getMoves(int roundNumber, boolean flat) {
		List<Move> result = new LinkedList<>();
		List<Move> all = (flat) ? getMovesFlat() : gameState.getMoves();
		for (Move m : all) {
			if (m.getRoundNumber() == roundNumber) {
				result.add(m);
			}
		}
		return result;
	}
	
	public Move getMove(Player player, int roundNumber, int moveIndex) {
		Move m = gameState.getMove(player, roundNumber, GameState.MoveAccessMode.ROUND_NUMBER);
		try {
			return m.getMoves().get(moveIndex);
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("The specified move index is not valid.", e);
		}
	}
	
	/**
	 * Get the very last Move or Sub Move (if Multi Move) of the
	 * specified Player.
	 * @param player
	 * @return the last Single Move
	 */
	public Move getLastMoveFlat(Player player) {
		Move m = gameState.getLastMove(player);
		if (m != null) {
			List<Move> ms = flattenMove(m, true);
			return ms.get(ms.size() - 1);
		}
		return null;
	}
	
	/**
	 * Gibt den gegebenen Move als flache Liste mit den einzelnen Moves zurueck.
	 * Wenn der Move ein Single Move ist, hat die Liste nur ein Element.
	 * @param move der Move, der flach gemacht werden soll
	 * @param includeBaseMove gibt an, ob bei Multi Moves der Base Move ebenfalls
	 * in der Liste stehen soll
	 * @return eine Liste mit Moves, die keine Sub Moves mehr enthalten
	 */
	public List<Move> flattenMove(Move move, boolean includeBaseMove) { // base move or root move ?
		List<Move> result = new LinkedList<>();
		
		// move selbst adden, wenn er keine sub moves hat 
		// oder wenn er nicht weggelassen werden soll.
		if (move.getMoves().isEmpty() || includeBaseMove) {
			result.add(move);
		}
		
		for (Move m : move.getMoves()) {
			result.addAll(flattenMove(m, includeBaseMove));
		}
		
		return result;
	}
	
	/**
	 * Ruft <tt>flattenMove(Move, boolean)</tt> so auf, dass bei Multi Moves
	 * der Base Move - der die Sub Moves enthaelt - nicht in der resultierenden
	 * Liste vorkommt (d.h. <tt>includeBaseMove == false</tt>, Standardverhalten).
	 * @param move
	 * @return
	 */
	public List<Move> flattenMove(Move move) {
		return flattenMove(move, false);
	}
	
	public Set<StationVertex> getDetectivePositions(int roundNumber) {
		
		if (roundNumber < 0) {
			throw new IllegalArgumentException("roundNumber must be greater or equal 0, but is: " + roundNumber);
		}
		
		Set<StationVertex> result = new HashSet<>();
		
		for (DetectivePlayer d : gameState.getDetectives()) {
			// Gibt's zufaellig nen Move zur roundNumber?
			Move move = gameState.getMove(d, roundNumber,
					GameState.MoveAccessMode.ROUND_NUMBER);
			
			// Nein?
			if (move == null) {
	
				boolean stop = false;
				ListIterator<Move> it = moveIterator(d, false);
				
				while (it.hasNext() && !stop) {
					Move m = it.next();
					
					// Eigentlich sollte < reichen, weil == oben schon abgehandelt ist.
					if (m.getRoundNumber() <= roundNumber) {						
						move = m;
					} else {
						stop = true;
					}
				}
			}
			
			if (move != null) {
				result.add(move.getStation());
			}
		}
		
		return result;
	}
	
	/**
	 * Gibt die jeweils letzten Stationen (Positions)
	 * der Detectives zurueck.
	 * @return Set with positions of the detectives.
	 */
	public Set<StationVertex> getDetectivePositions() {
		Set<StationVertex> result = new HashSet<>();
		
		for (DetectivePlayer d : gameState.getDetectives()) {
			Move m = gameState.getLastMove(d);
			if (m != null) {
				result.add(m.getStation());
			}
		}
		
		return result;
	}
		
	public Item getItem(Player player, Class<? extends Item> itemType) {
		for (Item item : gameState.getItems(player)) {
			// Apparently there is exactly one unique runtime representation per class.
			if (item.getClass() == itemType) { // item.getClass().equals(itemType)) {
				return item;
			}
		}
		return null;
	}

}
