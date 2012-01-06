package kj.scotlyard.game.ticket;

import kj.scotlyard.graph.ConnectionEdge;
import kj.scotlyard.graph.TaxiConnection;

public class TaxiTicket extends Ticket {

	@Override
	public boolean isValidFor(ConnectionEdge connection) {
		return (connection instanceof TaxiConnection);
	}

}
