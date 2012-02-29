package kj.scotlyard.game.player;

import kj.scotlyard.game.Move;

public class Player {

	private Move move;
	
	private PlayingPiece piece;
	
	public Player(PlayingPiece piece) {
		this.piece = piece;
	}
	
	public PlayingPiece getPlayingPiece() {
		return piece;
	}
	
	
	public void determineMove(Move move) {
		this.move = move;
	}
	
	public Move move() {
		Move m = move;
		move = null;
		return m;
	}
	
	public boolean isReady() {
		return (move != null);
	}
	
}