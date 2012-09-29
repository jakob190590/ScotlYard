package kj.scotlyard.game.ai.mrx;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.item.BlackTicket;
import kj.scotlyard.game.model.item.BusTicket;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.TaxiTicket;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.model.item.UndergroundTicket;
import kj.scotlyard.game.util.GameStateExtension;
import kj.scotlyard.game.util.MoveHelper;
import kj.scotlyard.game.util.MoveProducer;
import kj.scotlyard.game.util.SubMoves;

import org.apache.log4j.Logger;
import org.jgrapht.alg.BellmanFordShortestPath;

public class SimpleMrXAi extends AbstractMrXAi {
	
	private final static Logger logger = Logger.getLogger(SimpleMrXAi.class);
	
	private static class Alternative {
		public ConnectionEdge e1;
		public StationVertex v1;
		public ConnectionEdge e2;
		public StationVertex v2;
		public double costs;
		public double rating;
		public Alternative(ConnectionEdge e1, StationVertex v1, ConnectionEdge e2, StationVertex v2) {
			this.e1 = e1;
			this.v1 = v1;
			this.e2 = e2;
			this.v2 = v2;
		}
		public Alternative(ConnectionEdge e1, StationVertex v1) {
			this(e1, v1, null, null);
		}
		public boolean isDoubleMove() {
			return v2 != null;
		}
	}

	private Move result;

	public SimpleMrXAi(GameState gameState, GameGraph gameGraph) {
		super(gameGraph);
		setGameState(gameState);
	}

	@Override
	public Move move() {
		return result;
	}

	@Override
	protected void startCalculation() {
		result = null;
		beginCalculation();
		logger.info("begin calculation");
		
		
		Set<Alternative> alternatives = _1_alternatives();
		_2_rating(alternatives);
		Alternative bestAlternative = _3_select(alternatives);
		
		Move move = makeMove(bestAlternative);

		
		result = move;
		finishCalculation();
		logger.info("finish calculation");
	}

	protected Set<Alternative> _1_alternatives() {
		// 1. Alle Stationen ermitteln, wo er hinfahren könnte (nicht vergessen: Tickets und DoubleMoves)
		/*
		 * - Alle direkten Nachbarn als Alternativen eintragen
		 * - Wenn DoubleMoveCard da ist: Alle direkten Nachbarn
		 *   von allen bisher eingetragenen Alternativen als
		 *   weitere Alternativen hinzufuegen
		 * - Fuer jede Alternative:
		 *    * Moeglichen Ticketeinsatz betrachten:
		 *    * Alternative loeschen, wenn wegen Tickets nicht moeglich
		 *    * Alternative bewerten, je nach BILLIGSTEM Ticketeinsatz
		 * 
		 */
		
		int nDoubleMoveCards = 0;
		int nTaxiTickets = 0;
		int nBusTickets = 0;
		int nUndergroundTickets = 0;
		int nBlackTickets = 0;
		for (Item i : getGameState().getItems(getGameState().getMrX())) {
			if (i instanceof DoubleMoveCard)    nDoubleMoveCards++;    else
			if (i instanceof TaxiTicket)        nTaxiTickets++;        else
			if (i instanceof BusTicket)         nBusTickets++;         else
			if (i instanceof UndergroundTicket) nUndergroundTickets++; else
			if (i instanceof BlackTicket)       nBlackTickets++;
		}
		
		Set<Alternative> alternatives = new HashSet<>();
		Move lm = getGameState().getLastMove(getGameState().getMrX());
		StationVertex startingPosition = lm.getStation();
		for (ConnectionEdge e : startingPosition.getEdges()) {
			StationVertex v = e.getOther(startingPosition);
			alternatives.add(new Alternative(e, v));
		}
		if (nDoubleMoveCards > 0) {
			for (Alternative a : alternatives) {
				for (ConnectionEdge e : a.v1.getEdges()) {
					StationVertex v = e.getOther(startingPosition);
					alternatives.add(new Alternative(a.e1, a.v1, e, v));
				}
			}
		}
		
		// Alternativen aussortieren/bewerten
		Iterator<Alternative> it = alternatives.iterator();
		while (it.hasNext()) {
			Alternative a = it.next();
			
			// Annahme: Wir haben genug von allen Tickets (ausser BlackTicket!)
			int nUsedBlackTicket = 0;
			if (a.e1 instanceof FerryConnection) {
				nUsedBlackTicket++;
			}
			if (a.e2 instanceof FerryConnection) {
				nUsedBlackTicket++;
			}
			
			// Move nicht nicht moeglich
			if (nUsedBlackTicket > nBlackTickets) {
				it.remove();
				
			// Double Move
			} else if (a.isDoubleMove()) {
				
				if (nUsedBlackTicket == 2) {
					// Zwei BlackTickets eingesetzt
					a.costs = 1;
				} else if (nUsedBlackTicket == 1) {
					// Ein BlackTicket eingesetzt
					a.costs = .8;
				} else {
					// Nur normale Tickets, aber halt Double Move
					a.costs = .6;
				}
			
			// Single Move
			} else if (nUsedBlackTicket == 1) {
				a.costs = .3;
			} else {
				// Nur normale Tickets
				a.costs = 0;
			}
		}
		
		return alternatives;
	}
	
	protected void _2_rating(Set<Alternative> alternatives) {
		// 2. Für alle diese Stationen:
	    //  * Shortest Path zu jedem Detektiv
	    //  * Distanzen gesammelt bewerten

		for (Alternative a : alternatives) {
			StationVertex v = (a.isDoubleMove()) ? a.v2 : a.v1;
			int distances[] = new int[getGameState().getDetectives().size()];
			int i = 0;
			for (DetectivePlayer d : getGameState().getDetectives()) {
				StationVertex vd = getGameState().getLastMove(d).getStation();
				// TODO eigentlich brauch ich ALLE shortest paths
				List<ConnectionEdge> path = BellmanFordShortestPath.findPathBetween(getGameGraph().getGraph(), v, vd);
				boolean impossible = false;
				for (ConnectionEdge e : path) {
					if (e instanceof FerryConnection) {
						impossible = true;
						break;
					}
				}
				if (!impossible) {
					distances[i] = path.size();
				}
				i++;
			}
			
			double rating = 0;
			for (int d : distances) {
				rating += 1. / (d * d);
			}
			rating /= distances.length;
			a.rating = 1 - rating;
		}
	}
	
	protected Alternative _3_select(Set<Alternative> alternatives) {
		// 3. Station mit der günstigsten Bewertung wird ausgewählt
		Alternative best = new Alternative(null, null); // nur um den anfangswert 0 fuer rating zu haben
		for (Alternative a : alternatives) {
			double result = 0;
			if (a.rating > best.rating) {
				best = a;
			}
		}
		return best;
	}
	
	private Move makeMove(Alternative bestAlternative) {
		Ticket t1 = null;
		Ticket t2 = null;
		if (bestAlternative.e1 instanceof FerryConnection) {
			t1 = (Ticket) GameStateExtension.getItem(getGameState(),
					getGameState().getMrX(), BlackTicket.class);
		}
		Move m;
		if (bestAlternative.isDoubleMove()) {
			if (bestAlternative.e2 instanceof FerryConnection) {
				t2 = (Ticket) GameStateExtension.getItem(getGameState(),
						getGameState().getMrX(), BlackTicket.class);
			}
			
			if (t1 == null) {
				if (bestAlternative.rating < .85) {
					t1 = (Ticket) GameStateExtension.getItem(getGameState(),
							getGameState().getMrX(), BlackTicket.class);
				} else {
					t1 = MoveHelper.cheapTicket(bestAlternative.e1, getGameState().getItems(getGameState().getMrX()));
				}
			}
			if (t2 == null) {
				if (bestAlternative.rating < .8) {
					t2 = (Ticket) GameStateExtension.getItem(getGameState(),
							getGameState().getMrX(), BlackTicket.class);
				} else {
					t2 = MoveHelper.cheapTicket(bestAlternative.e1, getGameState().getItems(getGameState().getMrX()));
				}
			}
			
			DoubleMoveCard card = (DoubleMoveCard) GameStateExtension.getItem(getGameState(),
					getGameState().getMrX(), DoubleMoveCard.class);
			SubMoves sms = new SubMoves()
					.add(bestAlternative.v1, bestAlternative.e1, t1)
					.add(bestAlternative.v2, bestAlternative.e2, t2);
			m = MoveProducer.createMultiMove(getGameState().getMrX(),
					getGameState().getCurrentRoundNumber(),
					getGameState().getLastMove(getGameState().getMrX()).getMoveNumber() + 1,
					card, sms);
			
		} else {
			if (t1 == null) {
				if (bestAlternative.rating < .85) {
					t1 = (Ticket) GameStateExtension.getItem(getGameState(),
							getGameState().getMrX(), BlackTicket.class);
				} else {
					t1 = MoveHelper.cheapTicket(bestAlternative.e1, getGameState().getItems(getGameState().getMrX()));
				}
			}
			m = MoveProducer.createSingleMove(getGameState().getMrX(),
					getGameState().getCurrentRoundNumber(),
					getGameState().getLastMove(getGameState().getMrX()).getMoveNumber() + 1,
					bestAlternative.v1, bestAlternative.e1, t1);
		}
		return m;
	}

}
