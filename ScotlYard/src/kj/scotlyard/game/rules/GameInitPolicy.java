package kj.scotlyard.game.rules;

import java.util.Set;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.items.Item;

public interface GameInitPolicy {
	
	Set<Item> createItemSet(GameState gameState, Player player);
	
	StationVertex suggestInitialStation(GameState gameState, GameGraph gameGraph, Player player);

}
