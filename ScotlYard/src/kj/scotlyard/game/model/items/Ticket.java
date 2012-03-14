package kj.scotlyard.game.model.items;


public class Ticket extends Item {
	
	public enum Type {

		TAXI,
		BUS,
		UNDERGROUND,
		FERRY,
		
		BLACK
		
	}
	
	private TicketType ticketType;
	
	public TicketType getTicketType() {
		return ticketType;
	}

	public Ticket(TicketType ticketType) {
		this.ticketType = ticketType;
	}

}
