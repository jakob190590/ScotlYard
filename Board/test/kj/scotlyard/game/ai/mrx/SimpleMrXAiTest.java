package kj.scotlyard.game.ai.mrx;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import kj.scotlyard.board.BoardGraphLoader;
import kj.scotlyard.game.control.GameController;
import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.control.impl.DefaultGameController;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DefaultGame;
import kj.scotlyard.game.model.DefaultGameState;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.rules.TheRules;
import kj.scotlyard.game.util.MoveProducer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

public class SimpleMrXAiTest {
	static { PropertyConfigurator.configure("log4j-test.properties"); } // TODO voruebergehend
	private static final Logger logger = Logger.getLogger(SimpleMrXAiTest.class);
	
	private static final int TEST_DURCHLAEUFE = 20;
	
	Rules r;
	GameController gc;
	Game g;
	GameState gs;
	GameGraph gg;
	Map<Integer, StationVertex> nsm;
	
	MrXAi xAi;


	@Before
	public void setUp() throws Exception {
		r = new TheRules();
		
		
		// Momentan wird noch das Standard Board geladen ...
		
		// Board laden
		BoardGraphLoader bgl = new BoardGraphLoader();
		try {
			bgl.load("graph-description", "initial-stations");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		nsm = bgl.getNumberStationMap();
		
		gg = bgl.getGameGraph();
	
		g = new DefaultGame();
		gs = new DefaultGameState(g);
		
		gc = new DefaultGameController(g, gg, r);
		
		xAi = new SimpleMrXAi();
		xAi.setGameState(gs);
		xAi.setGameGraph(gg);
		
	}
	
	@Test
	public final void testMove() {
		for (int i = 0; i < TEST_DURCHLAEUFE; i++) {
			gc.clearPlayers();
			gc.newMrX();
			for (int j = 0; j < 4; j++) {
				gc.newDetective();
			}
			gc.newGame();
			
			logger.info("game controller: starting game");
			gc.start();
			
			while (gc.getStatus() == GameStatus.IN_GAME) {
				if (gs.getCurrentPlayer() instanceof MrXPlayer) {
					logger.info("mrX' turn");
					assertTrue(xAi.isReady());
					gc.move(xAi.move());
				} else {
					gc.move(MoveProducer.createNextBestSingleMove(gs, gg));
				}
			}
			logger.info("gameWin: " + gc.getWin());
		}
	}

}
