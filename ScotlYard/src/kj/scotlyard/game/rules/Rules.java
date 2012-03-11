package kj.scotlyard.game.rules;

public interface Rules {
	
	GameStateAccessPolicy getGameStateAccessPolicy();

	GameInitPolicy getGameInitPolicy();
	
	MovePolicy getMovePolicy();
	
	TurnPolicy getTurnPolicy();
	
	GameWinPolicy getGameWinPolicy();
	
}
