package kj.scotlyard.game.rules;

import java.util.Set;

import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.items.Item;
import kj.scotlyard.game.model.PlayingPiece;

public interface GameInitPolicy {
	
	Set<Item> createItemSet(PlayingPiece piece); // keine factory method! gibts was anderes fuer create? make? (selbes probl. wie bei game state access policy)
	
	StationVertex suggestInitialStation(PlayingPiece piece); // Station klingt ned soo gut... hm?

}
