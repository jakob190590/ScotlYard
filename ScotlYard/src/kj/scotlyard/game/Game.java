package kj.scotlyard.game;

import kj.scotlyard.game.player.MrXPiece;
import kj.scotlyard.game.player.PlayingPiece;

public interface Game extends GameState {
	
	void setMrX(MrXPiece piece);
	
	void setCurrentPlayer(PlayingPiece piece);
	
	void setCurrentRound(int round);
	
}
