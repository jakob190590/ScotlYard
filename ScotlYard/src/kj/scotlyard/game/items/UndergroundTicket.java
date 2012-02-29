package kj.scotlyard.game.items;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.UndergroundConnection;

public class UndergroundTicket extends Ticket {

	@Override
	public boolean isValidFor(ConnectionEdge connection) {
		return (connection instanceof UndergroundConnection);
	}

}
