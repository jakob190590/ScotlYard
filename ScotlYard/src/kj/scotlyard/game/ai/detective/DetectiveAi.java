package kj.scotlyard.game.ai.detective;

import java.util.List;

import kj.scotlyard.game.ai.Ai;
import kj.scotlyard.game.model.Move;

public interface DetectiveAi extends Ai {

	List<Move> getMoves();
	
}
