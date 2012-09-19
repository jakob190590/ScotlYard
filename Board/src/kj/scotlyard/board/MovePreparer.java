package kj.scotlyard.board;

import java.util.HashSet;
import java.util.Set;

import kj.scotlyard.game.graph.Connection;
import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.util.GameStateExtension;
import kj.scotlyard.game.util.MoveHelper;
import kj.scotlyard.game.util.MoveProducer;
import kj.scotlyard.game.util.SubMoves;

// Template method pattern
public abstract class MovePreparer {
	
	private GameState gs;
	
	private GameGraph gg;
	
	/**
	 *  Roher Datenbehaelter; aus einigen seiner Felder wird bei 
	 *  <code>getMove()</code> der resultierende Move erzeugt!
	 */
	private Move move = null;

	/**
	 * Resets current move preparation.
	 */
	public void reset() {
		move = null;
	}
	
	
	protected abstract void errorImpossibleNextStation(StationVertex station); // TODO reason: occupied or unreachable or ... ?

	/**
	 * Selects one ticket of the set and returns that ticket. The method may
	 * return <code>null</code> to cancel the move preparation. But this would 
	 * be not equivalent to <code>reset</code>: If there is a multi move
	 * is in preparation, only the new sub move would be discarded.
	 * 
	 * This method will be called by <code>nextStation</code>. Implementations
	 * can ask the user to select one ticket.
	 * @param tickets a set of possible tickets
	 * @return one of the passed tickets or <code>null</code>
	 */
	protected abstract Ticket selectTicket(Set<Ticket> tickets);

	
	/**
	 * A template method for building a Move.
	 * This algorithm uses the <code>protected abstract</code> methods.
	 * This way the user interaction can be customized.
	 * @param station
	 */
	public void nextStation(StationVertex station) {
		Move lm = gs.getLastMove(gs.getCurrentPlayer()); // last move
		StationVertex lastStation = lm.getStation();
		
		Move m = new DefaultMove();
		m.setStation(station);
		
		// Calculate all connections from current station to station
		Set<ConnectionEdge> connections = gg.getGraph().getAllEdges(lastStation, station);
		// TODO next station muss auch noch anderweitig geprueft werden (besetzt, kein ticket, ...?)
		if (connections.isEmpty()) {
			errorImpossibleNextStation(station);
		}
		
		Set<Ticket> tickets = new HashSet<>();
		for (ConnectionEdge c : connections) {
			// TODO addAll...
		}
				
		Ticket ticket = selectTicket(tickets);
		
		if (ticket != null) {		
			// D.h. nicht abgebrochen
			ConnectionEdge conn = MoveHelper.suggestConnection(lastStation, station, ticket);
			m.setConnection(conn);
			m.setItem(ticket);
			
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
		
	/**
	 * Crafts a turnkey Move from the preceding input (builder instructions). 
	 * This Move can be passed to the GameController.
	 * @return a turnkey Move
	 */
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
				DoubleMoveCard doubleMoveCard = (DoubleMoveCard) gsx.getItem(gs.getCurrentPlayer(), DoubleMoveCard.class);
				SubMoves sms = new SubMoves();
				for (Move m : move.getMoves()) {
					sms.add(m.getStation(), m.getConnection(), (Ticket) m.getItem());
				}
				result = MoveProducer.createMultiMove(gs.getCurrentPlayer(), gs.getCurrentRoundNumber(),
						moveNumber, doubleMoveCard, sms);
			}
		}		
		return result;
	}

}
