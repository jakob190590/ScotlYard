package kj.scotlyard.game.model;

@SuppressWarnings("serial")
public class CorruptGameStateException extends RuntimeException {

	public CorruptGameStateException() {
		super();
	}

	public CorruptGameStateException(String message) {
		super(message);
	}
	
	public CorruptGameStateException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
