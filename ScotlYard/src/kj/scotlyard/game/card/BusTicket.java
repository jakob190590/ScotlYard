package kj.scotlyard.game.card;

import kj.scotlyard.game.graph.BusConnection;
import kj.scotlyard.game.graph.ConnectionEdge;

public class BusTicket extends TicketCard {

	@Override
	public boolean isValidFor(ConnectionEdge connection) {
		return (connection instanceof BusConnection);
	}

}
