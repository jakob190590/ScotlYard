package kj.scotlyard.game.ticket;

import kj.scotlyard.graph.ConnectionEdge;
import kj.scotlyard.graph.UndergroundConnection;

public class UndergroundTicket extends Ticket {

	@Override
	public boolean isValidFor(ConnectionEdge connection) {
		return (connection instanceof UndergroundConnection);
	}

}
