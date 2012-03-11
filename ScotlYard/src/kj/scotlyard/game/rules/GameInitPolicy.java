package kj.scotlyard.game.rules;

import java.util.Set;

import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.items.Item;

public interface GameInitPolicy {
	
	Set<Item> createItemSet(Player piece); // keine factory method! gibts was anderes fuer create? make? (selbes probl. wie bei game state access policy)
	
	StationVertex suggestInitialStation(Player piece); // Station klingt ned soo gut... hm?

}
