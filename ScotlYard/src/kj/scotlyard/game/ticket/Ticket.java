package kj.scotlyard.game.ticket;

import kj.scotlyard.graph.ConnectionEdge;
import kj.scotlyard.player.Player;

public abstract class Ticket {
	
	public abstract boolean isValidFor(ConnectionEdge connection);
	
	public void changeOwner(Player oldOwner, Player newOwner) {
		oldOwner.getTickets().remove(this);
		newOwner.getTickets().add(this);
	}

}
