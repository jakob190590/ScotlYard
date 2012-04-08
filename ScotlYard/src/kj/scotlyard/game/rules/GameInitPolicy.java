package kj.scotlyard.game.rules;

import java.util.Set;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.Item;

public interface GameInitPolicy {
	
	int getMinDetectiveCount();
	
	int getMaxDetectiveCount();
	
	StationVertex suggestInitialStation(GameState gameState, GameGraph gameGraph, Set<StationVertex> initialPositions, Player player);
	
	Set<Item> createItemSet(GameState gameState, Player player);

}
