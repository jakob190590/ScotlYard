package kj.scotlyard.game.items;

import kj.scotlyard.game.graph.ConnectionEdge;

public class BlackTicket extends Ticket {

	@Override
	public boolean isValidFor(ConnectionEdge connection) {
		return true;
	}

}
