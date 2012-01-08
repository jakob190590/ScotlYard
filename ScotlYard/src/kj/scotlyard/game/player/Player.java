package kj.scotlyard.game.player;

import java.util.HashSet;
import java.util.Set;

import kj.scotlyard.game.Move;
import kj.scotlyard.game.card.Card;
import kj.scotlyard.game.card.CardOwner;
import kj.scotlyard.game.card.TicketCard;
import kj.set.ObjectSet;


public abstract class Player implements CardOwner {

	private String name;
	
	private Set<Card> cards;
	
	private Move move;
	
	protected Player(String name) { // protected oder public? bei protected waere die Klasse auch ohne abstract nicht instanziierbar. 
		this.name = name;
		this.cards = new ObjectSet<>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
	public Set<Card> getCards() {
		return cards;
	}
	
	
	/**
	 * Liefert die Tickets (Teilmenge der Karten)
	 * des Spielers. Aenderungen im Ergebnis haben
	 * keine Auswirkung auf den Kartenbestand des
	 * Spielers! Benutzen Sie <tt>ticket.changeOwner</tt>.
	 * @return Menge der Tickets; Aenderungen hierin
	 * haben keine Auswirkung auf den Kartenbestand!
	 */
	/*public Set<TicketCard> getTickets() {
		Set<TicketCard> tickets = new HashSet<>();
		for (Card c : cards) {
			if (c instanceof TicketCard)
				tickets.add((TicketCard) c);
		}
		return tickets;
	}
	*/

	/**
	 * Entscheidet, welchen Zug der Player machen wird.
	 * Entspricht dem Setter fuer move.
	 * 
	 * Wird vom richtigen Spieler ueber GUI oder von der AI aufgerufen.
	 * Sobald festgelegt, ist der Player fertig (<tt>isReady()</tt>).
	 * 
	 * @param move der geplante nacheste Spielzug
	 */
	public void determineMove(Move move) { // oder willMove (will heisst auch entscheiden/festlegen)
		this.move = move;
	}
	
	/**
	 * Player fuehrt den festgelegten Zug aus.
	 * Entspricht dem Getter fuer move.
	 * 
	 * Wird vom GameController abgefragt, wenn Player
	 * an der Reihe und fertig (<tt>isReady()</tt>) ist.
	 * 
	 * Wird nach dem Abfragen zurueckgesetzt,
	 * da jeder Spielzug nur einmal ausgefuehrt
	 * werden kann.
	 * 
	 * @return den Spielzug des Players
	 */
	public Move move() { // oder makeMove
		Move m = move;
		move = null;
		return m;
	}
	
	/**
	 * Gibt an, ob sich der Player fuer einen Zug
	 * entschieden hat.
	 * 
	 * @return <tt>true</tt>, wenn sich der Player fuer einen
	 * Zug entschieden hat.
	 */
	public boolean isReady() {
		return (move != null);
	}
	
}
