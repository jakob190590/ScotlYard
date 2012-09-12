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

import org.junit.Before;
import org.junit.Test;

public class TheGameInitPolicyTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public final void testGetMinDetectiveCount() {
		// trivial
	}

	@Test
	public final void testGetMaxDetectiveCount() {
		// trivial
	}

	@Test
	public final void testCreateItemSet() {
		// fast trivial
	}

	@Test
	public final void testSuggestInitialStation() {
		// fast trivial
//		Game g = new DefaultGame();
//		for (Player pl : g.getPlayers()) {
//			assertTrue(gg.getInitialStations().contains(g.getMove(pl, GameState.INITIAL_ROUND_NUMBER,
//					GameState.MoveAccessMode.ROUND_NUMBER).getStation()));
//		}
	}

}
