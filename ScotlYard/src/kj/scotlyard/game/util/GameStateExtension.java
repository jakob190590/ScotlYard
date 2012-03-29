package kj.scotlyard.game.util;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;

public class GameStateExtension {
	
	private GameState gameState;
	
	public GameStateExtension(GameState gameState) {
		this.gameState = gameState;
	}

	public Move getMove(Player player, int roundNumber, int moveIndex) {
		Move m = gameState.getMove(player, roundNumber, GameState.MoveAccessMode.ROUND_NUMBER);
		return m.getMoves().get(moveIndex); // hier koennts index out of bound exception geben, illegal arg waere besser oder?
	}
	
	public List<Move> getMoves(int roundNumber) {
		// TODO implement
		return null;
	}
	
	// flat (moveNumber) oder hierarchisch (roundNumber) ?
	public ListIterator<Move> moveIterator(Player player, int moveNumber) { // oder roundNumber?
		// TODO implement
		return null;
	}
		
	public ListIterator<Move> moveIterator(Player player) {
		return moveIterator(player, GameState.INITIAL_);
	}
	
	public List<Move> flattenMove(Move move) {
		List<Move> result = new Vector<>();
		result.add(move);
		for (Move m : move.getMoves()) {
			result.addAll(flattenMove(m));
		}
		return result;
	}
	
	public List<Move> getAllMoves() {
		List<Move> result = new LinkedList<>();
		for (Move m : gameState.getMoves()) {
			// auch das hier koennte man effizienter machen:
			// if (m.getMoves().isEmpty()) result.add(m); else
			result.addAll(flattenMove(m));
		}
		return result;
	}

}
