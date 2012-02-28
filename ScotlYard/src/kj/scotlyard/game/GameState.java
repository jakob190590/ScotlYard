package kj.scotlyard.game;

import java.util.List;

import kj.scotlyard.game.player.DetectivePlayer;
import kj.scotlyard.game.player.MrXPlayer;
import kj.scotlyard.game.player.Player;

public interface GameState {
	
	int INITIAL_ROUND = 0;
	
	MrXPlayer getMrX();
	
	List<DetectivePlayer> getDetectives();
	
	List<Player> getPlayers();
	
	List<Move> getMoves();
	
	int getCurrentRound();
	
	Player getCurrentPlayer();
	
	// Convenient methods
	
	List<Move> getMovesOfRound(int round);
	
	List<Move> getMovesOfCurrentRound();
	
	Move getMove(Player player, int round);
	
	Move getLastMove(Player player);
	
	// To add and remove Listeners

}
