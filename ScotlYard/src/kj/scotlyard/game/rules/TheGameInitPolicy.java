package kj.scotlyard.game.rules;

import java.util.HashSet;
import java.util.Set;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.model.GameState;
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
	public Station suggestInitialStation(GameState gameState,
			GameGraph gameGraph, Player player) {
		
		// es gibt 18 startkarten, die verlost werden.
		// TODO 
		return null;
	}
	
	@Override
	public Set<Item> createItemSet(GameState gameState, Player player) {	
		Set<Item> set = new HashSet<>();
		
		if (player == gameState.getMrX()) {
			addNTimes(set, 4, TaxiTicket.class);
			addNTimes(set, 3, UndergroundTicket.class);
			addNTimes(set, 3, BusTicket.class);
			addNTimes(set, 2, DoubleMoveCard.class);
			addNTimes(set, gameState.getDetectives().size(), BlackTicket.class);
		} else if (gameState.getDetectives().contains(player)) {
			addNTimes(set, 10, TaxiTicket.class);
			addNTimes(set, 8, BusTicket.class);
			addNTimes(set, 4, UndergroundTicket.class);
		} else {
			throw new IllegalArgumentException("Specified player is not part of the game.");
		}
		
		return set;
	}

}
