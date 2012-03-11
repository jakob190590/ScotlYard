package kj.scotlyard.game.ai;

import kj.scotlyard.game.control.GameStateRequester;
import kj.scotlyard.game.model.Move;

public interface Ai extends GameStateRequester {

	Move move();
	
	boolean isReady();
	
	void decideNow(); // oder determineNow
	
	int getTimeLeft(); // estimated, in millis
	
	int getTimeLimit();
	
	void setTimeLimit();
	
	
	void addAiListener(AiListener listener);
	
	void removeAiListener(AiListener listener);
	
}
