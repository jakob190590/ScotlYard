package kj.scotlyard.game.rules;

import java.util.HashSet;
import java.util.Set;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.BlackTicket;
import kj.scotlyard.game.model.item.BusTicket;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.TaxiTicket;
import kj.scotlyard.game.model.item.UndergroundTicket;

public class TheGameInitPolicy implements GameInitPolicy {
	
	private void addNTimes(Set<Item> set, int n, Class<? extends Item> item) {
		try {
			for (int i = 0; i < n; i++) {			
				set.add(item.newInstance());			
			}
		} catch (Exception e) {
			throw new RuntimeException("Does not work for given item class", e);
		}
	}	
	
	@Override
	public int getMinDetectiveCount() {
		// geht aus sonderregel hevor, dass wenn nur 2 spieler 
		// gegen mrX sind, duerfen sie je 2 spielfiguren haben.
		return 3;
	}
	
	@Override
	public int getMaxDetectiveCount() {
		return 5;
	}

	@Override
	public StationVertex suggestInitialStation(GameState gameState,
			GameGraph gameGraph, Player player) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Set<Item> createItemSet(GameState gameState, Player player) {	
		Set<Item> set = new HashSet<>();
		
		if (player instanceof MrXPlayer) {
			addNTimes(set, 4, TaxiTicket.class);
			addNTimes(set, 3, UndergroundTicket.class);
			addNTimes(set, 3, BusTicket.class);
			addNTimes(set, 2, DoubleMoveCard.class);
			addNTimes(set, gameState.getDetectives().size(), BlackTicket.class);
		} else if (player instanceof DetectivePlayer) {
			addNTimes(set, 10, TaxiTicket.class);
			addNTimes(set, 8, BusTicket.class);
			addNTimes(set, 4, UndergroundTicket.class);
		} else {
			throw new IllegalArgumentException("Invalid Player type.");
		}
		
		return set;
	}

}
