package kj.scotlyard.game.rules;

import java.util.List;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.CorruptGameStateException;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Player;

public class TheTurnPolicy implements TurnPolicy {
	
	private static final MovePolicy movePolicy = new TheMovePolicy();

	@Override
	public Turn getNextTurn(GameState gameState, GameGraph gameGraph) {
		
		// Zu Spielbeginn
		if (gameState.getMoves().isEmpty()) {
			// oder (getCurrentPlayer() == null) -- nein, weil wenn current player 
			// unter dem spiel null is, muss es die exception unten geben! Also ist 
			// moves.isEmpty() die "einzig wahre" Bedingung.
			
			return new Turn(gameState.getMrX(), GameState.INITIAL_ROUND_NUMBER);
		}
		
		List<Player> players = gameState.getPlayers();
		Player player = gameState.getCurrentPlayer();
		
		boolean found = false;
		for (Player p : players) {
			if (found && (gameState.getLastMove(p) == null || movePolicy.canMove(gameState, gameGraph, p))) {
				return new Turn(p, gameState.getCurrentRoundNumber());
			}
			
			if (p == player) {
				found = true;
			}
		}
		
		if (found) {
			// Current Player gefunden, aber alle folgenden Detectives 
			// koennen nicht ziehen, oder er war der Letzte der Runde
			// -> dann ist wieder MrX (der Erste) dran.
			return new Turn(gameState.getMrX(), gameState.getCurrentRoundNumber() + 1);
		} else {
			// Das ist so eindeutig, das muss gemeldet werden.
			throw new CorruptGameStateException("Current player is not part of the game.");			
		}
	}

}
