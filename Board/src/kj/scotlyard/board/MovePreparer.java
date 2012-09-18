package kj.scotlyard.board;

import java.util.Set;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.item.Ticket;

public class MovePreparer {
	
	private Move move = null;
	
	private ConnectionSelectionCallback callback;
	
	public static interface ConnectionSelectionCallback {
		ConnectionEdge selectConnection(Set<ConnectionEdge> connections);
	}
	
	public void registerConnectionSelectionCallback(ConnectionSelectionCallback callback) {
		this.callback = callback;
	}
	
	public void reset() {
		move = null;
	}
	
	
	public void nextStation(StationVertex station) {
		Move m = new DefaultMove(); // oder mit MoveProducer erzeugen
		m.setStation(station);
		
		// Calculate all connections from current station to station
		Set<ConnectionEdge> connections = ; 
		
		ConnectionEdge c = null;
		if (callback != null) {
			c = callback.selectConnection(connections);
		}
		if (c != null) {
			m.setConnection(c);
			// Publish m as move
		}
	}
	
	public void useTicket(Ticket ticket) {
		
	}
	
	public Move getMove() {
		return null;
	}

}
