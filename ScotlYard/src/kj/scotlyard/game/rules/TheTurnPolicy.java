package kj.scotlyard.game.rules;

import java.util.List;

import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Player;

public class TheTurnPolicy implements TurnPolicy {

	@Override
	public Player getNextPlayer(GameState gameState) {
		List<Player> players = gameState.getPlayers();
		Player p = gameState.getCurrentPlayer();
		
		if (p == null) {
			return players.get(0);
		}
		
		int i = players.indexOf(p);
		if (i < 0 || i >= (players.size() - 1)) {
			return players.get(0);
		}
		
		p = players.get(i + 1);
		//if (new TheMovePolicy().canMove(gameState, gameGraph, p));
		return p;		
	}

	@Override
	public int getNextRoundNumber(GameState gameState) {
		List<Player> players = gameState.getPlayers();
		Player p = gameState.getCurrentPlayer();
		int r = gameState.getCurrentRoundNumber();
		
		if (p == null || r < GameState.INITIAL_ROUND_NUMBER) {
			return GameState.INITIAL_ROUND_NUMBER;
		}
		
		if (players.indexOf(p) == (players.size() - 1)) {
			return r + 1;
		}
		
		return r;
	}

}
