package kj.scotlyard.game.util;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.items.Item;

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

	public List<Move> getMovesFlat() {
		List<Move> result = new LinkedList<>();
		for (Move m : gameState.getMoves()) {
			// auch das hier koennte man effizienter machen:
			// if (m.getMoves().isEmpty()) result.add(m); else
			result.addAll(flattenMove(m, true));
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
	
	public Move getLastMoveFlat(Player player) {
		Move m = gameState.getLastMove(player);
		if (m != null) {
			List<Move> ms = m.getMoves();
			if (!ms.isEmpty()) {
				m = ms.get(ms.size() - 1);
			}
		}
		return m;
	}
	
	public List<Move> flattenMove(Move move, boolean omitBaseMove) { // base move or root move ?
		List<Move> result = new LinkedList<>();
		
		// move selbst adden, wenn er keine sub moves hat 
		// oder wenn er nicht weggelassen werden soll.
		if (move.getMoves().isEmpty() || !omitBaseMove) {
			result.add(move);
		}
		
		for (Move m : move.getMoves()) {
			result.addAll(flattenMove(m, omitBaseMove));
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
