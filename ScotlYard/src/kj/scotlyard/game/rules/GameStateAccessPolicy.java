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

import kj.scotlyard.game.model.GameState;

public interface GameStateAccessPolicy {
	
	/**
	 * List that contains the MrX uncover move numbers
	 * in ascending order. The list can be empty but
	 * must not be <code>null</code>.
	 * @return a ascending list of MrX uncover move numbers
	 */
	List<Integer> getMrXUncoverMoveNumbers();
	
	/**
	 * Creates an GameState proxy for the detective players
	 * that may restrict the access. E.g.
	 * <code>TheGameStateAccessPolicy</code> restricts the
	 * access to MrX moves: Stations and connections are not
	 * accessible except in MrX uncover moves.
	 * 
	 * @param gameState
	 * @return a GameState proxy for detectives
	 */
	GameState createGameStateForDetectives(GameState gameState);

}
