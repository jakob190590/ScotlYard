/*
 * ScotlYard -- A software implementation of the Scotland Yard board game
 * Copyright (C) 2012  Jakob Schöttl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package kj.scotlyard.game.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.BlackTicket;
import kj.scotlyard.game.model.item.BusTicket;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.TaxiTicket;
import kj.scotlyard.game.model.item.UndergroundTicket;
import kj.scotlyard.game.util.GameStateExtension;

public class TheGameInitPolicy implements GameInitPolicy {
	
	private static Random random = new Random();
	
	private void addNTimes(Set<Item> set, int n, Class<? extends Item> item) {
		try {
			for (int i = 0; i < n; i++) {			
				set.add(item.newInstance());			
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Does not work for given item class.", e);
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
	
	@Override
	public StationVertex suggestInitialStation(GameState gameState,
			GameGraph gameGraph, Player player) {
		
		// es gibt 18 startkarten, die verlost werden.
		
		// die moeglichen startpositionen gehoeren hiermit
		// aber nicht zu den regeln, sondern zum Graph!
		
		// Possible initial positions
		List<StationVertex> poss = new ArrayList<>(gameGraph.getInitialStations());
		for (Move m : new GameStateExtension(gameState).getMoves(GameState.INITIAL_ROUND_NUMBER, false)) {
			poss.remove(m.getStation());
		}
		
		if (poss.size() == 0) {
			throw new IllegalArgumentException("Too few possible initial positions specified for this game.");
		}
		
		return poss.get(random.nextInt(poss.size()));
	}
	
}
