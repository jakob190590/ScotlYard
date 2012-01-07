package kj.scotlyard.game.card;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.TaxiConnection;

public class TaxiTicket extends TicketCard {

	@Override
	public boolean isValidFor(ConnectionEdge connection) {
		return (connection instanceof TaxiConnection);
	}

}
