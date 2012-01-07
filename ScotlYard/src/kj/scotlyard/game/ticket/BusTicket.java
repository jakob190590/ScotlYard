package kj.scotlyard.game.ticket;

import kj.scotlyard.graph.BusConnection;
import kj.scotlyard.graph.ConnectionEdge;

public class BusTicket extends Ticket {

	@Override
	public boolean isValidFor(ConnectionEdge connection) {
		return (isValid() && (connection instanceof BusConnection));
	}

}
