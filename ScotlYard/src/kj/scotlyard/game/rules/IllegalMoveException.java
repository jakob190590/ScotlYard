package kj.scotlyard.game.rules;

public class IllegalMoveException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

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
