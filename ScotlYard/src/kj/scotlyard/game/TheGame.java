package kj.scotlyard.game;

import java.util.List;

import kj.scotlyard.game.player.DetectivePlayer;
import kj.scotlyard.game.player.MrXPlayer;
import kj.scotlyard.game.player.Player;
import kj.scotlyard.game.rules.MovePolicy;

public class TheGame implements Game {
	
	private MovePolicy movePolicy;
		
	private MrXPlayer mrX;
	private DetectivePlayer[] detectives;
	private Player[] players; // all players, mrX is first
	
	@Override
	public MrXPlayer getMrX() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<DetectivePlayer> getDetectives() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Player> getPlayers() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Player getCurrentPlayer() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setCurrentPlayer(Player player) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<Move> getMoves() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Move getLastMoveOf(Player player) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void carryOut(Move move) {
		// TODO Auto-generated method stub
		try {
			movePolicy.checkValidness(move);
			moves.add(move);
		} catch (Exception e) {
			
		}
		
	}
	
}
