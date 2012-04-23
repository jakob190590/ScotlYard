package kj.scotlyard.game.util;

import java.util.Iterator;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.rules.MovePolicy;
import kj.scotlyard.game.rules.TheMovePolicy;

// Zum Klassennamen:
//    Keine Factory im Sinne des Abstract Factory Pattern
//    (auch wenn's in Java die BorderFactory gibt)
// -> Deswegen was Eigenes: Producer
public class MoveProducer {
	
	private MoveProducer() { }

	/**
	 * Creates and returns a new instance of TheMoveProducer.
	 * At least every thread should have it's own Producer.
	 * @return a new instance to work with
	 */
	public static MoveProducer createInstance() {
		return new MoveProducer(); 
	}	
	
	
	// Initial Move
	
	public Move createInitialMove(Player player, int roundNumber, StationVertex station) {
		
		// roundNumber muss nicht INITIAL_ROUND_NUMBER sein! Player kann auch spaeter joinen.
		return new DefaultMove(player, roundNumber, 
				GameState.INITIAL_MOVE_NUMBER, Move.NO_MOVE_INDEX, station, null, null);
	} 
	
	public Move createInitialMove(Player player, StationVertex station) {
		
		return createInitialMove(player, GameState.INITIAL_ROUND_NUMBER, station);
	} 
	
	
	// Single Move
	
	public Move createSingleMove(Player player, int roundNumber, int moveNumber, 
			StationVertex station, ConnectionEdge connection, Ticket ticket) {
		
		return new DefaultMove(player, roundNumber, moveNumber, 
				Move.NO_MOVE_INDEX, station, connection, ticket);
	}
	
	
	
	// Multi Move
	
	public Move createMultiMove(Player player, int roundNumber, 
			int firstMoveNumber, DoubleMoveCard card, SubMoves subMoves) {
		
		Move m = new DefaultMove(player, roundNumber, Move.NO_MOVE_NUMBER, 
				Move.NO_MOVE_INDEX, null, null, card);
		
		int i = 0;
		Move n = null;
		Iterator<Move> it = subMoves.iterator();
		while (it.hasNext()) {
			n = it.next();
			n.setPlayer(player);
			n.setRoundNumber(roundNumber);
			n.setMoveNumber(firstMoveNumber + i);
			n.setMoveIndex(i++);
			m.getMoves().add(n);
		}
		m.setStation((n == null) ? null : n.getStation());

		return m;
	}
	
	
	
	// Next Best Single Move
	
	public Move createNextBestSingleMove(GameState gameState, GameGraph gameGraph) {
		
		MovePolicy movePolicy = new TheMovePolicy();
		
		// Wir gehen von einem konsistenten GameState aus
		Move last = gameState.getLastMove(gameState.getCurrentPlayer());
		Move m = new DefaultMove(gameState.getCurrentPlayer(), gameState.getCurrentRoundNumber(), 
				last.getMoveNumber() + 1, Move.NO_MOVE_INDEX, null, null, null);
		
		StationVertex station = last.getStation();
		for (ConnectionEdge e : station.getEdges()) {
			for (Item i : gameState.getItems(gameState.getCurrentPlayer())) {
				if (i instanceof Ticket && movePolicy.isTicketValidForConnection((Ticket) i, e)) {
					
					StationVertex oStation = e.getOther(station);
					boolean vacant = true;
					for (Player p : gameState.getPlayers()) {
						if (p != last.getPlayer() && gameState.getLastMove(p).getStation() == oStation) {
							vacant = false;
							break;
						}
					}
					if (vacant) {
						m.setItem(i);
						m.setConnection(e);
						m.setStation(oStation);
						return m;
					}
				}
			}
		}
		
		throw new IllegalStateException("Current player cannot move.");
	}
}
