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

import static org.junit.Assert.*;

import java.util.Map;

import kj.scotlyard.board.BoardGraphLoader;
import kj.scotlyard.game.control.GameController;
import kj.scotlyard.game.control.impl.DefaultGameController;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DefaultGame;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.util.MoveProducer;
import kj.scotlyard.game.util.MrXTracker;

import org.junit.Before;
import org.junit.Test;

public class TheGameWinPolicyTest {

	Game g;
	GameGraph gg;
	Rules r;
	GameState dgs;
	MrXTracker tr;
	Map<Integer, StationVertex> nsm;
	
	MrXPlayer mrX;
	DetectivePlayer d1, d2, d3, d4;
	GameWinPolicy p;
	
	GameController ctrl;

	@Before
	public void setUp() throws Exception {
		
		BoardGraphLoader loader = new BoardGraphLoader();
		loader.load("graph-description", "initial-stations");
		gg = loader.getGameGraph();
		nsm = loader.getNumberStationMap();
		
		g = new DefaultGame();
		r = new TheRules();
		p = r.getGameWinPolicy();
		dgs = r.getGameStateAccessPolicy().createGameStateForDetectives(g);
		tr = new MrXTracker(g, gg, r);
				
		mrX = new MrXPlayer();
		d1 = new DetectivePlayer();
		d2 = new DetectivePlayer();
		d3 = new DetectivePlayer();
		d4 = new DetectivePlayer();

		g.setMrX(mrX);
		g.getDetectives().add(d1);
		g.getDetectives().add(d2);
		g.getDetectives().add(d3);
		g.getDetectives().add(d4);
		
		ctrl = new DefaultGameController(g, gg, r);

	}

	@Test
	public final void testIsGameWon() {
		
		ctrl.start();
		
		g.getMoves().add(MoveProducer.createInitialMove(mrX, 1, s(46)));
		g.getMoves().add(MoveProducer.createInitialMove(d1, 1, s(2))); // umzingelt von d3 u. d4
		g.getMoves().add(MoveProducer.createInitialMove(d2, 1, s(20)));
		g.getMoves().add(MoveProducer.createInitialMove(d3, 1, s(10)));
		g.getMoves().add(MoveProducer.createInitialMove(d4, 1, s(33)));
		
		// aber andere detectives koennen noch ziehen
		assertEquals(GameWin.NO, p.isGameWon(g, gg));
		
		g.getItems(d2).clear();
		g.getItems(d3).clear();
		g.getItems(d4).clear();
		
		// kein detective kann mehr ziehen
		assertEquals(GameWin.MRX_WINS, p.isGameWon(g, gg));
		
		g.getMoves().add(MoveProducer.createInitialMove(mrX, 1, s(21)));
		
		// mrx umzingelt
		assertEquals(GameWin.DETECTIVES_WIN, p.isGameWon(g, gg));
		
		g.getMoves().add(MoveProducer.createInitialMove(d1, 1, s(21))); // zieht auf mrX

		// mrx enttarnt, weil detective auf ihm steht
		assertEquals(GameWin.DETECTIVES_WIN, p.isGameWon(g, gg));
		
		
		// mrx hat bis zum ende durchgehalten
		ctrl.abort();
		ctrl.newGame();
		ctrl.start();
		
		for (int i = 1; i < 24; i++) {
			g.getMoves().add(MoveProducer.createSingleMove(mrX, i, i, s(46), null, null));
			g.getMoves().add(MoveProducer.createInitialMove(d1, i, s(2)));
			g.getMoves().add(MoveProducer.createInitialMove(d2, i, s(20)));
			g.getMoves().add(MoveProducer.createInitialMove(d3, i, s(10)));
			g.getMoves().add(MoveProducer.createInitialMove(d4, i, s(33)));
//			System.out.println(i);
			if (i >= 22)
				assertEquals(GameWin.MRX_WINS, p.isGameWon(g, gg));
			else
				assertEquals(GameWin.NO, p.isGameWon(g, gg));
		}
	}
	
	private StationVertex s(int number) {
		return nsm.get(number);
	}

}
