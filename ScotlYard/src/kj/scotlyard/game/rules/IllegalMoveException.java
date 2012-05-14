package kj.scotlyard.game.rules;

import kj.scotlyard.game.model.Move;

@SuppressWarnings("serial")
public class IllegalMoveException extends IllegalArgumentException {
	
	private Move move;

	public IllegalMoveException(String message, Move move, Throwable cause) {
		super(message, cause);
		this.move = move;
	}

	public IllegalMoveException(String message, Move move) {
		this(message, move, null);
	}
	
	public Move getMove() {
		return move;
	}

}
