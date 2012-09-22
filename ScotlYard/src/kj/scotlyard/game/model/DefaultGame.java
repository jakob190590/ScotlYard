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

import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import kj.scotlyard.game.model.item.Item;

public class DefaultGame extends AbstractGameState implements Game {

	private class PlayerNumberKey {
		
		private Player player;
		
		private int number;
		
		public PlayerNumberKey(Player player, int number) {
			this.player = player;
			this.number = number;
		}
		
		@Override
		public int hashCode() {
			// Von Eclipse erstellt
			final int prime = 31;
			int result = 1;
			result = prime * result + number;
			result = prime * result + ((player == null) ? 0 : player.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) {				
				return true;
			}
			if (obj instanceof PlayerNumberKey) {
				PlayerNumberKey key = (PlayerNumberKey) obj;
				if (key.player == player && key.number == number) {
					return true;
				}
			}
			return false;
		}
		
	}
	
	private class DetectiveList extends AbstractList<DetectivePlayer> {
		
		List<DetectivePlayer> list;

		public DetectiveList() { 
			list = new LinkedList<>();
		}
		
		/** Copy constructor */
		public DetectiveList(List<DetectivePlayer> detectives) {
			list = new LinkedList<>(detectives);
		}
		
		@Override
		public DetectivePlayer get(int index) {
			return list.get(index);
		}

		@Override
		public int size() {
			return list.size();
		}

		@Override
		public void add(int index, DetectivePlayer element) {
			
			if (list.contains(element)) {
				throw new IllegalArgumentException("This Player is already in the list!");
			}
			
			list.add(index, element);
			
			getPlayerListenerInformer().detectiveAdded(DefaultGame.this, element, index);
		}
		
		@Override
		public DetectivePlayer remove(int index) {
			DetectivePlayer p = list.remove(index);
			getPlayerListenerInformer().detectiveRemoved(DefaultGame.this, p, index);
			return p;
		}
		
	}
	
	private class MoveList extends AbstractList<Move> {

		List<Move> list;
		
		public MoveList() {
			list = new LinkedList<>();
		}
		
		/** Copy constructor */
		public MoveList(List<Move> moves) {
			list = new LinkedList<>(moves);
		}
		
		private List<Move> getMovesWithMoveNumber(Move move) {
			List<Move> mvs = new LinkedList<>();
			if (move.getMoveNumber() != Move.NO_MOVE_NUMBER) {
				mvs.add(move);
			}
			// Rekursion -- endet wenn move.getMoves().empty()
			for (Move m : move.getMoves()) {
				mvs.addAll(getMovesWithMoveNumber(m));
			}
			return mvs;
		}

		@Override
		public Move get(int index) {
			if (index < 0) {
				index = list.size() + index;
			}
			return list.get(index);
		}

		@Override
		public int size() {
			return list.size();
		}
		
		/**
		 * This method not only adds the Move to the list
		 * but also seals it (<code>seal()</code>)! Move 
		 * is Sealable because moves once added to the 
		 * history must not be alterable.
		 * @param e the move to add
		 */
		@Override
		public boolean add(Move e) {
			if (list.contains(e)) {
				throw new IllegalArgumentException("This Move is already in the list!");
			}
			
			// Spaetestens hier: Es wuerd uns grad noch fehlen, wenn jmd z.B. 
			// Move Number aendert, die wir im folgenden so schoen mappen.
			e.seal();			
			
			for (Move m : getMovesWithMoveNumber(e)) {
				movesByMoveNumber.put(new PlayerNumberKey(m.getPlayer(), m.getMoveNumber()), m);
			}
			movesByRoundNumber.put(new PlayerNumberKey(e.getPlayer(), e.getRoundNumber()), e);
			
			if (list.add(e)) {				
				getMoveListenerInformer().moveDone(DefaultGame.this, e);
				return true;
			}
						
			return false;
		}
		
		@Override
		public Move remove(int index) {
			if (index == LAST_MOVE) {
				Move mv = list.remove(list.size() - 1);
				
				for (Move m : getMovesWithMoveNumber(mv)) {
					movesByMoveNumber.remove(new PlayerNumberKey(m.getPlayer(), m.getMoveNumber()));
				}
				movesByRoundNumber.remove(new PlayerNumberKey(mv.getPlayer(), mv.getRoundNumber()));
				
				getMoveListenerInformer().moveUndone(DefaultGame.this, mv);
				
				return mv;
			}
			throw new IllegalArgumentException("You can only delete the last Move. Use GameState.LAST_MOVE as argument!");
		}
		
		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException("The method remove(Object) is not supported. Use remove(GameState.LAST_MOVE) instead.");
		}

		@Override
		public void clear() {
			
			// normal/efficient clear, not that from AbstractList!
			list.clear();

			movesByMoveNumber.clear();
			movesByRoundNumber.clear();
			
			getMoveListenerInformer().movesCleard(DefaultGame.this);			
		}
		
		
	}
	
	private class ItemSet extends AbstractSet<Item> {

		private Set<Item> set;
		
		private Player player;

		/**
		 * Auch eine Art Copy constructor:
		 * Es wird fuer intern ein neuer Set
		 * angelegt, der die Elemente von den
		 * uebergebenen items enthaelt. 
		 * @param player
		 * @param items
		 */
		public ItemSet(Player player, Set<Item> items) {
			this.player = player;
			
			// input kopieren, um sicherzustellen, dass this keine Dubletten zulaesst.
			set = new HashSet<>(items);
		}

		@Override
		public boolean add(Item e) {
			boolean ret = set.add(e);
			getItemListenerInformer().itemAdded(DefaultGame.this, player, e);
			return ret;
		}

		@Override
		public Iterator<Item> iterator() {			
			return new Iterator<Item>() {
				
				private Iterator<Item> it = set.iterator();
				private Item item;
				
				@Override
				public void remove() {
					it.remove();	
					// erst it.remove() -- somit werden listeners bei Exception nicht informiert
					getItemListenerInformer().itemRemoved(DefaultGame.this, player, item);
				}
				
				@Override
				public Item next() {
					item = it.next();
					return item;
				}
				
				@Override
				public boolean hasNext() {
					return it.hasNext();
				}
			};
		}

		@Override
		public int size() {
			return set.size();
		}		
		
	}
	
	private int currentRoundNumber;
	
	private Player currentPlayer;
	
	private MrXPlayer mrX;
	
	private List<DetectivePlayer> detectives = new DetectiveList();
	
	private Map<Player, Set<Item>> items = new HashMap<>();
	
	private List<Move> moves = new MoveList();
	
	private Map<PlayerNumberKey, Move> movesByRoundNumber = new HashMap<>();
	
	private Map<PlayerNumberKey, Move> movesByMoveNumber = new HashMap<>();

	@Override
	public DefaultGame copy() {
		DefaultGame g = new DefaultGame();
		
		g.mrX = mrX;
		g.detectives = new DetectiveList(detectives);
		
		// Player-Items-Map kopieren, Set fuer Set
		for (Player key : items.keySet()) {
			g.items.put(key, new ItemSet(key, items.get(key)));
		}
		
		g.moves = new MoveList(moves);
		
		g.currentPlayer = currentPlayer;
		g.currentRoundNumber = currentRoundNumber;
		
		// Listeners lassen wir natuerlich weg
		
		return g;
	}
	

	@Override
	public MrXPlayer getMrX() {
		return mrX;
	}

	@Override
	public List<DetectivePlayer> getDetectives() {		
		return detectives;
	}

	@Override
	public List<Player> getPlayers() {
		List<Player> list = new LinkedList<>();
		if (mrX != null) {
			list.add(mrX);
		}
		list.addAll(detectives);		
		return Collections.unmodifiableList(list);
	}

	@Override
	public Set<Item> getItems(Player player) {
		return items.get(player);
	}

	@Override
	public List<Move> getMoves() {
		return moves;
	}

	@Override
	public Move getMove(Player player, int number, MoveAccessMode accessMode) {
		PlayerNumberKey key;
		Move result;
		Move lastMove = getLastMove(player);
		
		switch (accessMode) {
		
		case ROUND_NUMBER:
			if (number < INITIAL_ROUND_NUMBER) {
				number = lastMove.getRoundNumber() + number + 1;
			}
			key = new PlayerNumberKey(player, number);
			result = movesByRoundNumber.get(key);
			break;
			
		case MOVE_NUMBER:
			if (number < INITIAL_MOVE_NUMBER) {
				int lastMoveNumber;
				
				int n = lastMove.getMoves().size();
				if (n > 0) {
					lastMoveNumber = lastMove.getMoves().get(n - 1).getMoveNumber();
				} else {
					lastMoveNumber = lastMove.getMoveNumber();
				}
				
				number = lastMoveNumber + number + 1;
			}
			key = new PlayerNumberKey(player, number);
			result = movesByMoveNumber.get(key);
			break;
			
		default:
			throw new IllegalArgumentException("MoveAccessMode must not be null.");
		}
		
		return result;
	}

	@Override
	public Move getLastMove(Player player) {
		ListIterator<Move> it = moves.listIterator(moves.size());
		while (it.hasPrevious()) {
			Move m = it.previous();
			if (m.getPlayer() == player) {
				return m;
			}
		}
		
		// No exception, if there is no last move.
		// Schliesslich ist es nicht verboten nach dem Move eines Players zu fragen,
		// auch wenn dieser seinen initial move halt grade noch nicht gemacht hat. 
		return null;
	}
	
	@Override
	public int getCurrentRoundNumber() {
		return currentRoundNumber;
	}

	@Override
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	@Override
	public void setMrX(MrXPlayer player) {
		MrXPlayer old = mrX;
		mrX = player;
		getPlayerListenerInformer().mrXSet(this, old, player);
	}

	@Override
	public void setItems(Player player, Set<Item> items) {		
		Set<Item> old = this.items.get(player);
		
		// Kein Item Set (null) ist auch erlaubt!
		// Klasse ItemSet ist besonderer Set, der eben die listener informiert, wenn sich was aendert.
		Set<Item> set = (items == null) ? null : new ItemSet(player, items);
		this.items.put(player, set);
		
		getItemListenerInformer().itemSetChanged(this, player, old, set);
	}

	@Override
	public void setCurrentRoundNumber(int roundNumber) {
		int old = currentRoundNumber;
		currentRoundNumber = roundNumber;
		getTurnListenerInformer().currentRoundChanged(this, old, roundNumber);
	}

	@Override
	public void setCurrentPlayer(Player player) {
		Player old = currentPlayer;
		currentPlayer = player;
		getTurnListenerInformer().currentPlayerChanged(this, old, player);
	}
	
}
