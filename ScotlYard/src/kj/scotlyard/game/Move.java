package kj.scotlyard.game;

import kj.scotlyard.game.card.TicketCard;
import kj.scotlyard.game.graph.StationVertex;

/**
 * Kapselt einen Spielzug eines Players.
 * 
 * Ein Zug referenziert die naechste Station und das
 * eingesetzte Ticket. Ein Zug kann sich ausserdem
 * aus mehreren Zuegen zusammensetzen (einfach verkettete
 * Liste).
 * 
 * @author jakob190590
 *
 */
public class Move {
	
	private StationVertex nextStation;
	private TicketCard ticket;
	
	private Move nextMove;

	public Move(StationVertex nextStation, TicketCard ticket, Move nextMove) {		
		this.nextStation = nextStation;
		this.ticket = ticket;
		this.nextMove = nextMove;
	}
	
	public Move(StationVertex nextStation, TicketCard ticket) {
		this(nextStation, ticket, null);
	}
	
	public Move() {
		this(null, null, null);
	}

	public StationVertex getNextStation() {
		return nextStation;
	}

	public void setNextStation(StationVertex nextStation) {
		this.nextStation = nextStation;
	}

	public TicketCard getTicket() {
		return ticket;
	}

	public void setTicket(TicketCard ticket) {
		this.ticket = ticket;
	}

	public Move getNextMove() {
		return nextMove;
	}

	public void setNextMove(Move nextMove) {
		this.nextMove = nextMove;
	}	

}
