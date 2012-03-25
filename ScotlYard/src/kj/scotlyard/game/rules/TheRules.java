package kj.scotlyard.game.rules;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.GameState;

public class TheRules implements Rules {
	
	GameStateAccessPolicy gsaP = new TheGameStateAccessPolicy();
	
	GameInitPolicy giP = new TheGameInitPolicy();
	
	MovePolicy mP = new TheMovePolicy();
	
	TurnPolicy tP = new TheTurnPolicy();
	
	GameWinPolicy gwP = new TheGameWinPolicy();

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
