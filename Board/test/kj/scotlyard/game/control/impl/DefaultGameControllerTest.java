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

package kj.scotlyard.game.control.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import kj.scotlyard.game.ai.Ai;
import kj.scotlyard.game.ai.AiListener;
import kj.scotlyard.game.ai.detective.DetectiveAi;
import kj.scotlyard.game.ai.mrx.MrXAi;
import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.DefaultGame;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.rules.TheRules;
import kj.scotlyard.game.util.GameStateExtension;

import org.junit.Before;
import org.junit.Test;

public class DefaultGameControllerTest {

	Rules r;

	Game g;
	GameGraph gg;
	DefaultGameController ctrl;
	
	GameStateExtension ext;
	
	@Before
	public void setUp() throws Exception {
		r = new TheRules();
		g = new DefaultGame();
		ctrl = new DefaultGameController(g, gg, r);
	}

	@Test
	public final void testEquipGameStateRequester() {
		Ai ai = new DetectiveAi() {
			@Override
			public void setGameState(GameState gameState) { }
			@Override
			public void setTimeLimit(int millis) { }
			@Override
			public void removeAiListener(AiListener listener) { }
			@Override
			public Move move() {
				return null;
			}
			@Override
			public boolean isReady() {
				return false;
			}
			@Override
			public int getTimeLimit() {
				return 0;
			}
			@Override
			public int getTimeLeft() {
				return 0;
			}
			@Override
			public void decideNow() { }
			@Override
			public void addAiListener(AiListener listener) { }
			@Override
			public List<Move> getMoves() {
				return null;
			}
			@Override
			public void setGameGraph(GameGraph gameGraph) {
				// TODO Auto-generated method stub
				
			}
		};
		ctrl.equipGameStateRequester(ai);
		// kann leider ned pruefen, ob ai jetzt den richtigen hat, hoechstens durch debuggen
		
		ai = new MrXAi() {
			@Override
			public Move move() {
				return null;
			}
			@Override
			public boolean isReady() {
				return false;
			}
			@Override
			public void decideNow() { }
			@Override
			public int getTimeLeft() {
				return 0;
			}
			@Override
			public int getTimeLimit() {
				return 0;
			}
			@Override
			public void setTimeLimit(int millis) { }
			@Override
			public void addAiListener(AiListener listener) { }
			@Override
			public void removeAiListener(AiListener listener) { }
			@Override
			public void setGameState(GameState gameState) { }
			@Override
			public void setGameGraph(GameGraph gameGraph) {
				// TODO Auto-generated method stub
				
			}
		};
		ctrl.equipGameStateRequester(ai);
		// kann leider ned pruefen, ob ai jetzt den richtigen hat, hoechstens durch debuggen
	}

	@Test
	public final void testGetStatus() {
		assertEquals(GameStatus.NOT_IN_GAME, ctrl.getStatus());
		assertEquals(GameWin.NO, ctrl.getWin());
	}

	@Test
	public final void testGetUndoManager() {
	}

	@Test
	public final void testNewGame() {
	}

	@Test
	public final void testClearPlayers() {
	}

	@Test
	public final void testNewMrX() {
	}

	@Test
	public final void testNewDetective() {
	}
		
	@Test
	public final void testRemoveDetective() {
	}

	@Test
	public final void testShiftUpDetective() {
	}

	@Test
	public final void testShiftDownDetective() {
	}

	@Test
	public final void testStart() {
	}

	@Test
	public final void testAbort() {
	}

	@Test
	public final void testMove() {
	}

}
