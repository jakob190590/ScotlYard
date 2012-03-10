package kj.scotlyard.game.ai;

import kj.scotlyard.game.control.GameGraphRequester;
import kj.scotlyard.game.control.GameStateRequester;
import kj.scotlyard.game.rules.Rules;

public interface Ai extends GameStateRequester, GameGraphRequester {

	void decideNow(); // oder determineNow
	
	void setRules(Rules rules);
	
}
