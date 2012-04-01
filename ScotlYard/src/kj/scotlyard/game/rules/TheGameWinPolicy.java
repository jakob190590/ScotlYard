package kj.scotlyard.game.rules;

import java.util.List;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.util.GameStateExtension;

public class TheGameWinPolicy implements GameWinPolicy {
	
	/** Is MrX surrounded in the specified round? */
	private boolean isMrXSurrounded(GameState gameState, GameGraph gameGraph, int roundNumber) {
		
		GameStateExtension ext = new GameStateExtension(gameState);
		List<Move> round = ext.getMoves(roundNumber, false);
		// damit hast du eine liste der moves der round (erstes element ist move von mrX).
		
		// TODO implement, korbi?
		
		return false;
	}

	@Override
	public GameWin isGameWon(GameState gameState, GameGraph gameGraph) {
		
		Player mrX = gameState.getMrX();
		Player lastDetective = gameState.getDetectives().get(gameState.getDetectives().size() - 1);
		
		// TODO zu spielbeginn is es gefaehrlich
		Move mrXLastMove = gameState.getLastMove(mrX);
		Move lastMove = gameState.getMoves().get(GameState.LAST_MOVE);
		
		
		// Detectives win wenn 
		// - MrX umzingelt ist (TODO Beleg fehlt mir)
		// - ein Detective auf MrX' Feld zieht
		
		// MrX wins wenn 
		// - MrX auf letztem Feld (dieser Tafel) angekommen ist
		// - Detectives nicht mehr ziehen koennen 
		
		// Reihenfolge der Tests/Bedingungspruefungen ist wichtig!
				
		
		// mrX sicher auf letztem feld angekommen
		if (mrXLastMove.getMoveNumber() >= 22) {
			// er kann ja nur dorthin gezogen sein, 
			// wenn da kein detective stand (MovePolicy)
			return GameWin.MRX_WINS;
		}
		
		// mrX umzingelt
		if (gameState.getCurrentPlayer() == mrX 
				&& isMrXSurrounded(gameState, gameGraph, gameState.getCurrentRoundNumber() - 1)) {
			
			// d.h.wenn jetzt wieder mrX dran ist,
			// er aber umzingelt ist...
			return GameWin.DETECTIVES_WIN;
		}
		
		// mrX ertappt
		// TODO ist no ned testreif
		if (gameState.getCurrentPlayer() == mrX) {//lastMove.getPlayer() != mrX && lastMove.getStation() == mrXLastMove.getStation()) {
			for (DetectivePlayer detective : gameState.getDetectives()) {
				if (gameState.getLastMove(detective).getStation() == mrXLastMove.getStation()) {					
					return GameWin.DETECTIVES_WIN;
				}
			}
		}
		
		// detectives koennen nimmer ziehen
				
		
		
		return GameWin.NO;
	}

}
