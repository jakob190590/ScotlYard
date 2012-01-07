package kj.scotlyard.game.card;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.UndergroundConnection;

public class UndergroundTicket extends TicketCard {

	@Override
	public boolean isValidFor(ConnectionEdge connection) {
		return (connection instanceof UndergroundConnection);
	}

}
