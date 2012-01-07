package kj.scotlyard.game.card;

import kj.scotlyard.game.graph.ConnectionEdge;

public class BlackTicket extends TicketCard {

	@Override
	public boolean isValidFor(ConnectionEdge connection) {
		return true;
	}

}
