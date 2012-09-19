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

package kj.scotlyard.game.ai;

import kj.scotlyard.game.control.GameStateRequester;
import kj.scotlyard.game.model.Move;

/**
 * Interface for any AI that calculates moves for player(s).
 * The AI calculation is guided by GameState listeners, 
 * especially MoveListener. But the AI does not carry out
 * a move, it only provides the calculated move, see 
 * <code>move()</code>.
 * @author jakob190590
 *
 */
public interface Ai extends GameStateRequester {

	/**
	 * Returns the calculated AI move for the current player.
	 * @return the AI move for the current player
	 */
	Move move();
	
	boolean isReady();
	
	/**
	 * Suggests the calculation process, to come to an end.
	 * Note that also in this case the AI must provide a move,
	 * even it is a bad one.
	 */
	void decideNow(); // oder determineNow
	
	int getTimeLeft(); // estimated, in millis
	
	int getTimeLimit();
	
	void setTimeLimit();
	
	
	void addAiListener(AiListener listener);
	
	void removeAiListener(AiListener listener);
	
}
