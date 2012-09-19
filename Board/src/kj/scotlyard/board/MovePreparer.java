package kj.scotlyard.board;

import java.util.Set;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.item.Ticket;

// Template method pattern
public class MovePreparer {
	
	private GameState gs;
	
	private GameGraph gg;
	
	private Move move = null;

	// Reset move preparation
	public void reset() {
		move = null;
	}
	
	
	// Template method for algorithm in nextStation
	// Returning null means 'cancel'
	protected abstract ConnectionEdge selectConnection(Set<ConnectionEdge> connections);

	// Algorithm for preparing the move
	public void nextStation(StationVertex station) {
		Move lm = g.getLastMove(g.getCurrentPlayer); // last move
		
		Move m = new DefaultMove(); // oder mit MoveProducer erzeugen
		m.setStation(station);
		
		// Calculate all connections from current station to station
		Set<ConnectionEdge> connections = gg.getGraph().getAllEdges(lm.getStation(), station);
		
		ConnectionEdge conn = selectConnection(connections);
		
		if (conn != null) {
			// D.h. nicht abgebrochen
			m.setConnection(conn);
			m.setItem(); // TODO Item selbst raussuchen, passend zur conn! wie auch immer
			
			// Publish m as/in move
			if (move == null) {
				move = m;
			} else {				
				if (move.getMoves().isEmpty()) {
					// move ist noch kein Multi Move
					// -> move neuem Move als Sub Move adden
					Move n = new DefaultMove();
					n.getMoves().add(move);
					move = n;
				}
				move.getMoves().add(m);
			}
		}
	}
		
	public Move getMove() {		
		Move result = null;
		if (move != null) {
			int moveNumber = gs.getLastMove(gs.getCurrentPlayer()).getMoveNumber() + 1; // Exception abfangen? eher ned, den fall sollts ja nicht geben
			if (move.getMoves().isEmpty()) {
				// Single Move
				result = MoveProducer.createSingleMove(gs.getCurrentPlayer(), gs.getCurrentRoundNumber(), 
						moveNumber,
						move.getStation(), move.getConnection(), (Ticket) move.getItem());
			} else {
				// Multi Move
				GameStateExtension gsx = new GameStateExtension(gs);
				Item doubleMoveCard = gsx.getItem(gs.getCurrentPlayer(), DoubleMoveCard.class)
				SubMoves sms = new SubMoves();
				for (Move m : move.getMoves()) {
					sms.add(m);
				}
				result = MoveProducer.createMultiMove(gs.getCurrentPlayer(), gs.getCurrentRoundNumber(),
						moveNumber, doubleMoveCard, sms);
			}
		}		
		return result;
	}

}
