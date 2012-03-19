package kj.scotlyard.game.rules;

import java.util.Set;

import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.items.Item;

public interface GameInitPolicy {
	
	Set<Item> createItemSet(Player player);
	
	StationVertex suggestInitialStation(Player player);

}
