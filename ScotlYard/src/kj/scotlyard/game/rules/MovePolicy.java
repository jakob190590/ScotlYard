package kj.scotlyard.game.rules;

import kj.scotlyard.game.Move;

public interface MovePolicy {	
	
	void checkMove(Move move) throws IllegalMoveException;
	
}
