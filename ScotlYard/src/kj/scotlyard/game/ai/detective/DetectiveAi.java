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

package kj.scotlyard.game.ai.detective;

import java.util.List;

import kj.scotlyard.game.ai.Ai;
import kj.scotlyard.game.model.Move;

/**
 * This interface extends the Ai interface, to obtain all
 * detective moves in the current round. This is because
 * it is reasonable that the detective AI always calculates 
 * for all detectives.
 * @author jakob190590
 *
 */
public interface DetectiveAi extends Ai {

	/**
	 * Returns a list with all detective moves for the current round.// TODO unmodifiable list?
	 * The order of the moves corresponds with the order of the detectives in the GameState.
	 * @return a list with all detective moves in the current round
	 */
	List<Move> getMoves();
	
}
