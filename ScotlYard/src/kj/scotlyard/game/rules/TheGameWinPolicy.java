package kj.scotlyard.game.rules;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.GameState;

public class TheGameWinPolicy implements GameWinPolicy {

	@Override
	public GameWin isGameWon(GameState gameState, GameGraph gameGraph) {
		// TODO Auto-generated method stub
		
		// MrX wins wenn Detektive nicht mehr ziehen koennen oder MrX auf letztem Feld (dieser Tafel) angekommen ist.
		// Detectives win wenn ein Detective auf MrX' Feld zieht, oder MrX umzingelt ist.
		return null;
	}

}
