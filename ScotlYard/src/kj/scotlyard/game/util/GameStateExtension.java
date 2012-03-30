package kj.scotlyard.game.util;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.items.Item;

public class GameStateExtension {
	
	private GameState gameState;
	
	public GameStateExtension(GameState gameState) {
		this.gameState = gameState;
	}

	public List<Move> getMovesFlat() {
		List<Move> result = new LinkedList<>();
		for (Move m : gameState.getMoves()) {
			// auch das hier koennte man effizienter machen:
			// if (m.getMoves().isEmpty()) result.add(m); else
			result.addAll(flattenMove(m, false));
		}
		return result;
	}
	
	public ListIterator<Move> moveIterator(Player player, boolean flat, int moveRespRoundNumber) {
		List<Move> all = (flat) ? getMovesFlat() : gameState.getMoves();
		// TODO implement
		return null;
	}
	
	public ListIterator<Move> moveIterator(Player player, boolean flat) {
		return moveIterator(player, flat, 0);
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
		List<Move> ms = m.getMoves();
		if (!ms.isEmpty()) {
			m = ms.get(ms.size() - 1);
		}
		return m;
	}
	
	public List<Move> flattenMove(Move move, boolean alsoReturnRootMove) {
		List<Move> result = new LinkedList<>();
		if (alsoReturnRootMove) {
			result.add(move);
		}
		for (Move m : move.getMoves()) {
			result.addAll(flattenMove(m, alsoReturnRootMove));
		}
		return result;
	}
	
	
	public Item getItem(Player player, Class<? extends Item> itemType) {
		for (Item item : gameState.getItems(player)) {
			// Apparently there is one unique runtime representation per class.
			if (item.getClass() == itemType) { // item.getClass().equals(itemType)) {
				return item;
			}
		}
		return null;
	}

}
