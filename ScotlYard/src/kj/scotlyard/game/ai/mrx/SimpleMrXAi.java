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
import kj.scotlyard.game.model.Move;
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
	
	private List<RatingModule> ratingModules = new ArrayList<>();
	
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
		
		for (Alternative a : alternatives) {
			logger.debug("alternative: " + a);
		}
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
		
		// Anzahl der Items
		Map<Class<? extends Item>, Integer> itemCounts = new HashMap<>();
		itemCounts.put(DoubleMoveCard.class, 0);
		itemCounts.put(TaxiTicket.class, 0);
		itemCounts.put(BusTicket.class, 0);
		itemCounts.put(UndergroundTicket.class, 0);
		itemCounts.put(BlackTicket.class, 0);
		for (Item i : getGameState().getItems(getGameState().getMrX())) {
			itemCounts.put(i.getClass(), itemCounts.get(i.getClass()) + 1);
		}
		
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
	
	protected Map<Alternative, Rating> getCumulativeRatings(Set<Alternative> alternatives) {
		
		// Die Rating-Ergebnisse aller Module in Liste eintragen
		List<Map<Alternative, Rating>> allRatings = new LinkedList<>();
		for (RatingModule rm : ratingModules) {
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
				// TODO korbi, deine hier
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
		Move m;
		if (bestAlternative.isDoubleMove()) {
			DoubleMoveCard card = (DoubleMoveCard) GameStateExtension.getItem(getGameState(),
					getGameState().getMrX(), DoubleMoveCard.class);
			Ticket t1 = null; // TODO muss 2 tickets bekommen, wenn beide vom gleichen typ sind, zwei VERSCHIEDENE geht nicht mit GameStateExt.getItem...
			Ticket t2 = null;
			SubMoves sms = new SubMoves()
					.add(bestAlternative.getVertex1(), bestAlternative.getEdge1(), t1)
					.add(bestAlternative.getVertex2(), bestAlternative.getEdge2(), t2);
			m = MoveProducer.createMultiMove(getGameState().getMrX(),
					getGameState().getCurrentRoundNumber(),
					getGameState().getLastMove(getGameState().getMrX()).getMoveNumber() + 1,
					card, sms);
			
		} else {
			m = MoveProducer.createSingleMove(getGameState().getMrX(),
					getGameState().getCurrentRoundNumber(),
					getGameState().getLastMove(getGameState().getMrX()).getMoveNumber() + 1,
					bestAlternative.getVertex1(), bestAlternative.getEdge1(),
					(Ticket) GameStateExtension.getItem(getGameState(),
					getGameState().getCurrentPlayer(), bestAlternative.getTicketType1()));
		}
		return m;
	}

	public List<RatingModule> getRatingModules() {
		return ratingModules;
	}

}
