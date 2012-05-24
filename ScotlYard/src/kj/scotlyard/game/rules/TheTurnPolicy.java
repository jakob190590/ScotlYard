/*
 * ScotlYard -- A software implementation of the Scotland Yard board game
 * Copyright (C) 2012  Jakob Schöttl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
