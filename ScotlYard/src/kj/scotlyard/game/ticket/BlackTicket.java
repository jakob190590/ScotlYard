package kj.scotlyard.game.ticket;

import kj.scotlyard.graph.ConnectionEdge;

public class BlackTicket extends Ticket {

	@Override
	public boolean isValidFor(ConnectionEdge connection) {
		return true;
	}

}
