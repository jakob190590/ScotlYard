package kj.scotlyard.game.rules;

import java.util.HashSet;
import java.util.Set;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.items.BlackTicket;
import kj.scotlyard.game.model.items.BusTicket;
import kj.scotlyard.game.model.items.DoubleMoveCard;
import kj.scotlyard.game.model.items.Item;
import kj.scotlyard.game.model.items.TaxiTicket;
import kj.scotlyard.game.model.items.UndergroundTicket;

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

	@Override
	public StationVertex suggestInitialStation(GameState gameState,
			GameGraph gameGraph, Player player) {
		// TODO Auto-generated method stub
		return null;
	}

}
