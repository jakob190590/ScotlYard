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
	
	protected static class ConnectionAndTicket {
		// TODO generate default constructor and constructor from both fields!
		public ConnectionEdge connection;
		public Ticket ticket;
	}
	
	// Template method for algorithm in nextStation
	// Returning null means 'cancel'
	protected abstract ConnectionAndTicket selectConnectionAndTicket(Set<ConnectionEdge> connections);
	
	// Reset move preparation
	public void reset() {
		move = null;
	}
	
	// Algorithm for preparing the move
	public void nextStation(StationVertex station) {
		Move lm = g.getLastMove(g.getCurrentPlayer); // last move
		
		Move m = new DefaultMove(); // oder mit MoveProducer erzeugen
		m.setStation(station);
		
		// Calculate all connections from current station to station
		Set<ConnectionEdge> connections = gg.getGraph().getAllEdges(lm.getStation(), station);
		
		ConnectionAndTicket connAndTicket = selectConnectionAndTicket(connections);
		
		if (connAndTicket != null) {
			// D.h. nicht abgebrochen
			m.setConnection(connAndTicket.connection);
			m.setItem(connAndTicket.ticket);
			
			// Publish m as/in move
			if (move == null) {
				move = m;
			} else {
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
						moveNumber,
						doubleMoveCard, // woher nehmen? einen getter, der null liefern kann, dann kommt der fehler bei der movepolicy, das ist zu spaet!
						sms);
			}
		}		
		return result;
	}

}
