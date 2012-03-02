package kj.scotlyard.game.model;


public interface Game extends GameState {
	
	void setMrX(MrXPiece piece);
	
	void setCurrentPlayer(PlayingPiece piece);
	
	void setCurrentRound(int round);
	
}
