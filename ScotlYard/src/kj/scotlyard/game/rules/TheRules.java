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
