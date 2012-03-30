package kj.scotlyard.game.model;

import java.util.LinkedList;
import java.util.List;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.items.Item;
import kj.scotlyard.game.model.items.Ticket;

// Zum Klassennamen:
// -  Keine Factory im Sinne des Abstract Factory Pattern
//    (auch wenn's in Java die BorderFactory gibt)
// -  Kein Builder im Sinne des Builder Pattern
// -> Deswegen was eigenes: Producer
public class TheMoveProducer {
	
	private static List<Move> subMoves = new LinkedList<>();

	
	// Initial Move
	
	public static Move createInitialMove(Player player, int roundNumber, StationVertex station) {
		
		// roundNumber muss nicht INITIAL_ROUND_NUMBER sein! Player kann auch spaeter joinen.
		return new DefaultMove(player, roundNumber, 
				GameState.INITIAL_MOVE_NUMBER, Move.NO_MOVE_INDEX, station, null, null);
	} 
	
	public static Move createInitialMove(Player player, StationVertex station) {
		
		return createInitialMove(player, GameState.INITIAL_ROUND_NUMBER, station);
	} 
	
	
	// Single Move
	
	public static Move createSingleMove(Player player, int roundNumber, int moveNumber, 
			StationVertex station, ConnectionEdge connection, Ticket ticket) {
		
		return new DefaultMove(player, roundNumber, moveNumber, 
				Move.NO_MOVE_INDEX, station, connection, ticket);
	}
	
	
	
	// Multi Move (Builder interface)
	
	public static void addSubMove(StationVertex station, 
			ConnectionEdge connection, Ticket ticket) {
		
		// Player and Numbers will be set later in createMultiMove
		subMoves.add(new DefaultMove(null, 0, Move.NO_MOVE_NUMBER, 
				Move.NO_MOVE_INDEX, station, connection, ticket));
	}
	
	public static void discardSubMoves() {
		subMoves.clear();
	}
	
	public static Move createMultiMove(Player player, int roundNumber, 
			int firstMoveNumber, Item item, Move ...moves) {
		
		Move m = new DefaultMove(player, roundNumber, Move.NO_MOVE_NUMBER, 
				Move.NO_MOVE_INDEX, null, null, item, moves);
		
		int i = 0;
		for (Move n : moves) {
			n.setPlayer(player);
			n.setRoundNumber(roundNumber);
			n.setMoveNumber(firstMoveNumber + i);
			n.setMoveIndex(i++);
		}
		m.setStation(moves[moves.length - 1].getStation());

		return m;
	}
}
