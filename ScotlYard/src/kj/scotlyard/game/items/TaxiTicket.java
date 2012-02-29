package kj.scotlyard.game.items;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.TaxiConnection;

public class TaxiTicket extends Ticket {

	@Override
	public boolean isValidFor(ConnectionEdge connection) {
		return (connection instanceof TaxiConnection);
	}

}
