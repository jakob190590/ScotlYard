/*
 * ScotlYard -- A software implementation of the Scotland Yard board game
 * Copyright (C) 2012  Jakob Schöttl, Korbinian Eckstein
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

package kj.scotlyard.game.ai.mrx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import kj.scotlyard.game.ai.mrx.modules.RatingModule;
import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.BlackTicket;
import kj.scotlyard.game.model.item.BusTicket;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.TaxiTicket;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.model.item.UndergroundTicket;
import kj.scotlyard.game.util.GameStateExtension;
import kj.scotlyard.game.util.MoveProducer;
import kj.scotlyard.game.util.SubMoves;

import org.apache.log4j.Logger;

public class SimpleMrXAi extends AbstractMrXAi {
	
	private final static Logger logger = Logger.getLogger(SimpleMrXAi.class);
	
	private Map<RatingModule, Double> ratingModules = new HashMap<>();
	
	private Move result;

	@Override
	public Move move() {
		return result;
	}

	@Override
	protected void startCalculation() {
		result = null;
		beginCalculation();
		logger.info("begin calculation");
		
		
		Set<Alternative> alternatives = getAlternatives();
		Map<Alternative, Rating> cumulativeRatings = getCumulativeRatings(alternatives);
		List<Alternative> bestAlternatives = getBestAlternatives(cumulativeRatings);
		
//		for (Alternative a : alternatives) {
//			logger.debug("alternative: " + a);
//		}
//		logger.info(String.format("alternative with best rating: doublemove=%b, rating=%f, costs=%f",
//				bestAlternative.isDoubleMove(), bestAlternative.rating, bestAlternative.costs));
		
		if (bestAlternatives.isEmpty()) {
			assert alternatives.isEmpty() : "Fehler in getBestAlternatives";
			logger.error("alternativlos. mrX haette gar nicht mehr drankommen duerfen, oder in getAlternatives ist ein fehler");
		} else {
			// bei mehreren gleichwertigen moves: zufaellig waehlen
			result = makeMove(bestAlternatives.get(new Random().nextInt(bestAlternatives.size())));
		}
		
		finishCalculation();
		logger.info("finish calculation");
	}

	public static Set<Class<? extends Ticket>> getValidTicketTypes(ConnectionEdge connection) {
		Set<Class<? extends Ticket>> result = new HashSet<>();
		if (connection instanceof TaxiConnection) {
			result.add(TaxiTicket.class);
			result.add(BlackTicket.class);
			return result;
		}
		if (connection instanceof BusConnection) {
			result.add(BusTicket.class);
			result.add(BlackTicket.class);
			return result;
		}
		if (connection instanceof UndergroundConnection) {
			result.add(UndergroundTicket.class);
			result.add(BlackTicket.class);
			return result;
		}
		if (connection instanceof FerryConnection) {
			result.add(BlackTicket.class);
			return result;
		}
		return result;
	}

	protected Set<Alternative> getAlternatives() {
		// Alle Stationen ermitteln, wo MrX hinfahren könnte
		/*
		 * - Alle direkten Nachbarn als Alternativen eintragen
		 * - Wenn DoubleMoveCard da ist: Alle direkten Nachbarn
		 *   von allen bisher eingetragenen Alternativen als
		 *   weitere Alternativen hinzufuegen
		 */
		
		Map<Class<? extends Item>, Integer> itemCounts = getItemCounts(getGameState(), getGameState().getMrX());
		
		// Detective Positions fallen gleich weg
		Set<StationVertex> detectivePositions = GameStateExtension
				.getDetectivePositions(getGameState());
		
		// Erreichbar durch Single Move
		Set<Alternative> alternatives = new HashSet<>();
		Move lm = getGameState().getLastMove(getGameState().getMrX());
		StationVertex startingPosition = lm.getStation();
		for (ConnectionEdge e : startingPosition.getEdges()) {
			StationVertex v = e.getOther(startingPosition);
			if (!detectivePositions.contains(v)) {
				for (Class<? extends Ticket> t : getValidTicketTypes(e)) {
					if (itemCounts.get(t) > 0) {
						alternatives.add(new Alternative(e, t, v));
					}
				}
			}
		}
		
		// Erreichbar durch Double Move
		Set<Alternative> furtherAlternatives = new HashSet<>();
		if (itemCounts.get(DoubleMoveCard.class) > 0) {
			for (Alternative a : alternatives) {
				for (ConnectionEdge e : a.getVertex1().getEdges()) {
					StationVertex v = e.getOther(a.getVertex1());
					if (!detectivePositions.contains(v)) {
						for (Class<? extends Ticket> t : getValidTicketTypes(e)) {
							if (itemCounts.get(t) > ((a.getTicketType1() == t) ? 1 : 0)) {
								furtherAlternatives.add(new Alternative(a.getEdge1(),
										a.getTicketType1(), a.getVertex1(), e, t, v));
							}
						}
					}
				}
			}
		}
		alternatives.addAll(furtherAlternatives);
		
		return alternatives;
	}

	public static Map<Class<? extends Item>, Integer> getItemCounts(GameState gameState, Player player) {
		Set<Item> items = gameState.getItems(player);
		if (items == null) {
			throw new IllegalArgumentException("No item set registered in GameState for the Player.");
		}
		// Anzahl der Items
		Map<Class<? extends Item>, Integer> itemCounts = new HashMap<>();
		itemCounts.put(DoubleMoveCard.class, 0);
		itemCounts.put(TaxiTicket.class, 0);
		itemCounts.put(BusTicket.class, 0);
		itemCounts.put(UndergroundTicket.class, 0);
		itemCounts.put(BlackTicket.class, 0);
		for (Item i : items) {
			itemCounts.put(i.getClass(), itemCounts.get(i.getClass()) + 1);
		}
		return itemCounts;
	}
	
	protected Map<Alternative, Rating> getCumulativeRatings(Set<Alternative> alternatives) {
		
		// Die Rating-Ergebnisse aller Module in Liste eintragen
		List<Map<Alternative, Rating>> allRatings = new LinkedList<>();
		for (Map.Entry<RatingModule, Double> entry : ratingModules.entrySet()) {
			RatingModule rm = entry.getKey();
			Double d = entry.getValue();
			double weight = (d == null) ? 1 : d;
			allRatings.add(rm.rate(getGameState(), getGameGraph(), getGameState().getCurrentPlayer(), alternatives));
		}
		
		// Gesamt-Ratings berechnen
		Map<Alternative, Rating> cumulativeRatings = new HashMap<>();
		for (Alternative a : alternatives) {
			double r = 1;
//			double r = 0;
			for (Map<Alternative, Rating> map : allRatings) {
				r *= map.get(a).rating; // ohne gewichtung * rechnen
//				r += map.get(a).rating; // ohne gewichtung summe bilden (fuer durchschnitt)
				// TODO korbi, deine hier, weight mit einbeziehen!
			}
//			r /= ratingModules.size(); // fuer durchschnitt
			cumulativeRatings.put(a, new Rating(r));
		}
		
		return cumulativeRatings;
	}
	
	protected List<Alternative> getBestAlternatives(Map<Alternative, Rating> cumulativeRatings) {
		List<Alternative> bestAlternatives = new ArrayList<>();
		Rating bestRating = new Rating(0);
		for (Map.Entry<Alternative, Rating> entry : cumulativeRatings.entrySet()) {
			Rating r = entry.getValue();
			if (r.equals(bestRating)) {
				// Genau gleiches Rating
				bestAlternatives.add(entry.getKey());
			} else if (r.compareTo(bestRating) > 0) {
				// r > bisher bestes Rating
				bestRating = r;
				bestAlternatives.clear();
				bestAlternatives.add(entry.getKey());
			}
		}
		logger.info(bestAlternatives.size() + " alternatives with best rating: " + bestRating);
		return bestAlternatives;
	}
	
	
	private Move makeMove(Alternative bestAlternative) {
		GameState gs = getGameState();
		MrXPlayer mrX = gs.getMrX();
		
		int roundNumber = gs.getCurrentRoundNumber();
		int moveNumber = GameStateExtension.getLastMoveFlat(gs, mrX).getMoveNumber() + 1;
		
		Ticket t1 = (Ticket) GameStateExtension.getItem(gs, mrX, bestAlternative.getTicketType1());
		assert t1 != null : "getAlternatives gibt wohl ungueltige alternativen";
		
		Move m;
		if (bestAlternative.isDoubleMove()) {
			DoubleMoveCard card = (DoubleMoveCard) GameStateExtension.getItem(gs,
					mrX, DoubleMoveCard.class);
			assert card != null : "getAlternatives gibt wohl ungueltige alternativen";
			
			Set<Item> items = new HashSet<>(gs.getItems(mrX)); // TODO javadoc von getItems: note that ItemsSet in a gameState is read only
			items.remove(t1);
			Ticket t2 = (Ticket) GameStateExtension.getItem(gs, mrX, bestAlternative.getTicketType2());
			assert t2 != null : "getAlternatives gibt wohl ungueltige alternativen";
			
			SubMoves sms = new SubMoves()
					.add(bestAlternative.getVertex1(), bestAlternative.getEdge1(), t1)
					.add(bestAlternative.getVertex2(), bestAlternative.getEdge2(), t2);
			m = MoveProducer.createMultiMove(mrX, roundNumber, moveNumber,
					card, sms);
			
		} else {
			m = MoveProducer.createSingleMove(mrX, roundNumber, moveNumber,
					bestAlternative.getVertex1(), bestAlternative.getEdge1(),
					t1);
		}
		return m;
	}

	public void addRatingModule(RatingModule ratingModule, Double weight) {
		// TODO check weight! -> IllegalArgumentException
		ratingModules.put(ratingModule, weight);
	}
	
	public void addRatingModule(RatingModule ratingModule) {
		addRatingModule(ratingModule, null);
	}
	
	public void removeRatingModule(RatingModule ratingModule) {
		ratingModules.remove(ratingModule);
	}

}
