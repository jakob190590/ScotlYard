package kj.scotlyard.game.model.items;


public class Ticket extends Item {
	
	private TicketType ticketType;
	
	public TicketType getTicketType() {
		return ticketType;
	}

	public Ticket(TicketType ticketType) {
		this.ticketType = ticketType;
	}

}
