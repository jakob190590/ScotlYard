package kj.scotlyard.game.rules;

import java.util.Set;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.Item;

public interface GameInitPolicy {
	
	int getMinDetectiveCount();
	
	int getMaxDetectiveCount();
	
	Station suggestInitialStation(GameState gameState, GameGraph gameGraph, Player player);
	
	Set<Item> createItemSet(GameState gameState, Player player);

}
