package kj.scotlyard.game.rules;

@SuppressWarnings("serial")
public class IllegalMoveException extends IllegalArgumentException {

	public IllegalMoveException() {
		super();
	}

	public IllegalMoveException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalMoveException(String s) {
		super(s);
	}

	public IllegalMoveException(Throwable cause) {
		super(cause);
	}

}
