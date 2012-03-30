package kj.scotlyard.game.model;

import java.util.Arrays;

import kj.scotlyard.game.model.items.Item;

public class TheMultiMove extends DefaultMove {

	public TheMultiMove(Player player, int roundNumber, int firstMoveNumber, Item item, Move ...moves) {
		
		setPlayer(player);
		setRoundNumber(roundNumber);
		setMoveNumber(Move.NO_MOVE_NUMBER);
		setMoveIndex(Move.NO_MOVE_INDEX);
		
		int i = 0;
		for (Move m : moves) {
			m.setPlayer(player);
			m.setRoundNumber(roundNumber);
			m.setMoveNumber(firstMoveNumber + i);
			m.setMoveIndex(i++);
		}
		setStation(moves[moves.length - 1].getStation());
		
		setConnection(null);
		setItem(item);
		getMoves().addAll(Arrays.asList(moves));
		
	}
	
}
