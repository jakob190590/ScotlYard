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

public class TheRules implements Rules {
	
	private GameStateAccessPolicy gsaP = new TheGameStateAccessPolicy();
	
	private GameInitPolicy giP = new TheGameInitPolicy();
	
	private MovePolicy mP = new TheMovePolicy();
	
	private TurnPolicy tP = new TheTurnPolicy();
	
	private GameWinPolicy gwP = new TheGameWinPolicy();

	@Override
	public GameStateAccessPolicy getGameStateAccessPolicy() {
		return gsaP;
	}

	@Override
	public GameInitPolicy getGameInitPolicy() {
		return giP;
	}

	@Override
	public MovePolicy getMovePolicy() {
		return mP;
	}

	@Override
	public TurnPolicy getTurnPolicy() {
		return tP;
	}

	@Override
	public GameWinPolicy getGameWinPolicy() {
		return gwP;
	}

}
