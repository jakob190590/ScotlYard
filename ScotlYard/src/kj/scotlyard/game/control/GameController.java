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

package kj.scotlyard.game.control;

import java.util.Observable;

import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.IllegalMoveException;

public abstract class GameController extends Observable {
	
	public abstract GameStatus getStatus();
	
	public abstract GameWin getWin();
	

	public abstract void newGame();
	
	public abstract void clearPlayers();
	
	
	public abstract void newMrX();
	
	public abstract void newDetective();
	
	public abstract void removeDetective(DetectivePlayer detective);
	
	public abstract void shiftUpDetective(DetectivePlayer detective);
	
	public abstract void shiftDownDetective(DetectivePlayer detective);
	
	
	public abstract void start();
	
	public abstract void abort();
	
	
	public abstract void move(Move move) throws IllegalMoveException;
	
}
