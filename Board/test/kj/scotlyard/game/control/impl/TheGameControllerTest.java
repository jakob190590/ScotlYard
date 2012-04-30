package kj.scotlyard.game.control.impl;

import static org.junit.Assert.*;

import java.util.List;

import kj.scotlyard.game.ai.Ai;
import kj.scotlyard.game.ai.AiListener;
import kj.scotlyard.game.ai.detective.DetectiveAi;
import kj.scotlyard.game.ai.mrx.MrXAi;
import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.control.impl.DefaultGameController;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.TheGame;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.rules.TheRules;
import kj.scotlyard.game.util.GameStateExtension;

import org.junit.Before;
import org.junit.Test;

public class TheGameControllerTest {

	Rules r;

	Game g;
	GameGraph gg;
	DefaultGameController ctrl;	
	
	GameStateExtension ext;
	
	@Before
	public void setUp() throws Exception {
		r = new TheRules();
		g = new TheGame();
		ctrl = new DefaultGameController(g, gg, null, r);
	}

	@Test
	public final void testEquipGameStateRequester() {
		Ai ai = new DetectiveAi() {			
			@Override
			public void setGameState(GameState gameState) { }			
			@Override
			public void setTimeLimit() { }			
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
			public void setTimeLimit() { }
			@Override
			public void addAiListener(AiListener listener) { }
			@Override
			public void removeAiListener(AiListener listener) { }
			@Override
			public void setGameState(GameState gameState) { }			
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
