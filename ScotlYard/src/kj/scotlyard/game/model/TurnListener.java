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

package kj.scotlyard.game.model;

public interface TurnListener {

	/**
	 * Is called by a Game when the turn comes from oldPlayer to newPlayer,
	 * provided that <code>oldPlayer != newPlayer</code>!
	 * Either oldPlayer or newPlayer may be <code>null</code> (normally only
	 * when <code>GameStatus.NOT_IN_GAME</code>).
	 * @param gameState
	 * @param oldPlayer
	 * @param newPlayer
	 */
	void currentPlayerChanged(GameState gameState, Player oldPlayer, Player newPlayer);
	
	/**
	 * Is called by a Game when the round comes from oldRoundNumber to newRoundNumber,
	 * provided that <code>oldRoundNumber != newRoundNumber</code>!
	 * Normally when <code>GameStatus.IN_GAME</code> the round number will 
	 * be incremented.
	 * @param gameState
	 * @param oldRoundNumber
	 * @param newRoundNumber
	 */
	void currentRoundChanged(GameState gameState, int oldRoundNumber, int newRoundNumber);
	
}
