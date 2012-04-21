package kj.scotlyard.game.util;

import java.util.LinkedList;
import java.util.List;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.Ticket;

// Zum Klassennamen:
// -  Keine Factory im Sinne des Abstract Factory Pattern
//    (auch wenn's in Java die BorderFactory gibt)
// -  Kein Builder im Sinne des Builder Pattern
// -> Deswegen was eigenes: Producer
public class MoveProducer {
	
	private List<Move> subMoves = new LinkedList<>();
	
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
	
	
	
	// Multi Move (Builder interface)	
	
	// Because of this methods, there must be at least one instance per thread.
	// With static methods instead, the multi move production would not be thread-safe. 
	
	public void addSubMove(StationVertex station, ConnectionEdge connection, Ticket ticket) {
		
		// Player and Numbers will be set later in createMultiMove
		subMoves.add(new DefaultMove(null, 0, Move.NO_MOVE_NUMBER, 
				Move.NO_MOVE_INDEX, station, connection, ticket));
	}
	
	public void discardSubMoves() {
		subMoves.clear();
	}
	
	public Move createMultiMove(Player player, int roundNumber, 
			int firstMoveNumber, DoubleMoveCard card) {
		
		Move m = new DefaultMove(player, roundNumber, Move.NO_MOVE_NUMBER, 
				Move.NO_MOVE_INDEX, null, null, card);
		
		
		int i = 0;
		for (Move n : subMoves) {
			n.setPlayer(player);
			n.setRoundNumber(roundNumber);
			n.setMoveNumber(firstMoveNumber + i);
			n.setMoveIndex(i++);
		}
		m.setStation(subMoves.get(subMoves.size() - 1).getStation());

		m.getMoves().addAll(subMoves);
		subMoves.clear();
		
		return m;
	}
}
