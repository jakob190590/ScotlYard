package kj.scotlyard.game.items;

import kj.scotlyard.game.graph.BusConnection;
import kj.scotlyard.game.graph.ConnectionEdge;

public class BusTicket extends Ticket {

	@Override
	public boolean isValidFor(ConnectionEdge connection) {
		return (connection instanceof BusConnection);
	}

}
