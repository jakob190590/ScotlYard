package kj.scotlyard.game.rules;

import java.util.List;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.CorruptGameStateException;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Player;

public class TheTurnPolicy implements TurnPolicy {
	
	private MovePolicy movePolicy = new TheMovePolicy();

	@Override
	public Player getNextPlayer(GameState gameState, GameGraph gameGraph) {
		
		List<Player> players = gameState.getPlayers();
		Player player = gameState.getCurrentPlayer();
		
		// Zu Spielbeginn
		if (player == null) { // oder getMoves().isEmpty();
			return gameState.getMrX();
		}
		
		boolean found = false;
		for (Player p : players) {
			if (found && movePolicy.canMove(gameState, gameGraph, p)) {
				return p;
			}
			
			if (p == player) {
				found = true;
			}
		}
		
		if (found) {
			// Current Player gefunden, aber alle folgenden Detectives koennen nicht ziehen
			// -> dann ist wieder MrX (der Erste) dran.
			return gameState.getMrX();
		} else {
			// Das ist so eindeutig, das muss gemeldet werden.
			throw new CorruptGameStateException("Current player is not part of the game.");			
		}
	}

	@Override
	public int getNextRoundNumber(GameState gameState, GameGraph gameGraph) {

		List<Player> players = gameState.getPlayers();
		Player player = gameState.getCurrentPlayer();
		
		// Zu Spielbeginn
		if (player == null) { // oder getMoves().isEmpty();
			return GameState.INITIAL_ROUND_NUMBER;
		}
		
		boolean found = false;
		for (Player p : players) {
			if (found && movePolicy.canMove(gameState, gameGraph, p)) {
				return gameState.getCurrentRoundNumber();
			}
			
			if (p == player) {
				found = true;
			}
		}
		
		if (found) {
			// Current Player gefunden, aber alle folgenden Detectives koennen nicht ziehen
			// -> dann ist wieder MrX (der Erste) dran.
			return gameState.getCurrentRoundNumber() + 1;
		} else {
			// Das ist so eindeutig, das muss gemeldet werden.
			throw new CorruptGameStateException("Current player is not part of the game.");			
		}
		
	}

}
