package kj.scotlyard.game;

import java.util.List;

import kj.scotlyard.game.player.DetectivePiece;
import kj.scotlyard.game.player.MrXPiece;
import kj.scotlyard.game.player.PlayingPiece;

public interface GameState {
	
	public enum MoveAccessMode {
        ROUND,
        MOVENUMBER
    }
	
	int INITIAL_ROUND = 0;
	
	MrXPiece getMrX();
	
	List<DetectivePiece> getDetectives();
	
	List<PlayingPiece> getPlayers();
	
	List<Move> getMoves();
	
	int getCurrentRound();
	
	PlayingPiece getCurrentPlayer();
	
	// Convenient methods
	
	List<Move> getMovesOfRound(int round);
	
	List<Move> getMovesOfCurrentRound();
	
	Move getMove(PlayingPiece player, int round);
	
	Move getLastMove(PlayingPiece player);
	
	// To add and remove Listeners

}
