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

package kj.scotlyard.game.ai.mrx;

import kj.scotlyard.game.ai.AbstractAi;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;

public abstract class AbstractMrXAi extends AbstractAi implements MrXAi {

	protected AbstractMrXAi(GameGraph gameGraph) {
		super(gameGraph);
	}

	@Override
	protected void moveUndone(Move move) { }

	@Override
	protected void moveDone(Move move) { }
	
	@Override
	protected void currentPlayerChanged(Player oldPlayer, Player newPlayer) {
		// Starten, immer wenn MrX dran kommt
		if (newPlayer instanceof MrXPlayer) {
			startCalculation();
		}
		// TODO andauernde calc auch abbrechen ??
	}

}
