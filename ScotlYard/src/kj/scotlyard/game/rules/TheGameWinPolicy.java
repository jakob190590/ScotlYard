package kj.scotlyard.game.rules;

import java.util.List;
import java.util.ListIterator;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;

public class TheGameWinPolicy implements GameWinPolicy {	

	@Override
	public GameWin isGameWon(GameState gameState, GameGraph gameGraph) {
		
		Player mrX = gameState.getMrX();
		List<Move> allMoves = gameState.getMoves();
		
		Move mrXLastMove = gameState.getLastMove(mrX);
		MovePolicy movePolicy = new TheMovePolicy();
		
		
		if (mrXLastMove != null) {
			
			
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
			
			// mrX ertappt
			// isGameWon wird ja nach jedem move abgefragt.
			// solln wir also hier
			// 1.) immer nur den letzten detective move testen, oder
			// 2.) den jeweils letzten move aller detectives, die nach mrX dran waren?
			// loesungsmoeglichkeit: hinten anfangen: dann laeufts im normalfall
			// auf 1.) raus, is aber trotzdem sicher wie 2.)		
			Move move;
			ListIterator<Move> it = allMoves.listIterator(allMoves.size());
			while (it.hasPrevious() && (move = it.previous()) != mrXLastMove) {
				if (move.getStation() == mrXLastMove.getStation()) {
					return GameWin.DETECTIVES_WIN;
				}
			}
			
			// mrX umzingelt
			if (gameState.getCurrentPlayer() == mrX 
					&& !movePolicy.canMove(gameState, gameGraph, mrX)) {
				
				// d.h. wenn jetzt wieder mrX dran ist,
				// er aber umzingelt ist...
				return GameWin.DETECTIVES_WIN;
			}
					
			
			// detectives koennen nimmer ziehen
			boolean noDetectiveCanMove = true;
			for (DetectivePlayer detective : gameState.getDetectives()) {
				if (movePolicy.canMove(gameState, gameGraph, detective)) {
					noDetectiveCanMove = false;
					break;
				}
			}
			if (noDetectiveCanMove) {
				return GameWin.MRX_WINS;
			}
			
			
		}
		
		return GameWin.NO;
	}

}
