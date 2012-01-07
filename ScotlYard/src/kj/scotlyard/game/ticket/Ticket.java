package kj.scotlyard.game.ticket;

import kj.scotlyard.graph.ConnectionEdge;
import kj.scotlyard.player.Player;

/**
 * Diese Klasse repraesentiert ein Ticket, mit dem der Spieler
 * von einer Station zur naechsten reisen kann.
 * 
 * Zu den Tickets zaehlen wirklich nur Fahrkarten, nicht die Karten
 * fuer die Doppelzuege des Mr. X ...
 * 
 * @author jakob190590
 *
 */
public abstract class Ticket {
	
	private boolean valid = true;
	
	/**
	 * Entwertet das Ticket. Damit ist es fuer immer ungueltig.
	 * Bei Scotland Yard wird das Ticket erst von MrX entwertet;
	 * bei Benutzung durch Detektive wird nur der Besitzer gewechselt!
	 */
	public void invalidiate() { // also validiate or cancel
		valid = false;
	}
	
	/**
	 * Gibt an ob die Fahrkarte grundsaetzlich noch gueltig ist,
	 * oder schon entwertet wurde.
	 * @return <tt>false</tt>, wenn die Fahrkarte schon entwertet wurde.
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Prueft, ob dieses Ticket fuer die angegebene Verbindung
	 * gueltig ist. Das Ticket sollte nur gueltig sein koennen,
	 * wenn es noch nicht entwertet wurde.
	 * @param connection die Verbindung
	 * @return <tt>true</tt>, wenn das Ticket gueltig ist
	 */
	public abstract boolean isValidFor(ConnectionEdge connection);
	
	/**
	 * Aendert den Besitzer des Tickets. In Scotland Yard koennen Tickets
	 * eigentlich nur von den Detektiven zum Mr. X wandern. Durch diese
	 * allgemeine Methode bin ich dafuer aber unabhaengiger von anderen
	 * Objekten wie MrX oder Game, das den MrX referenziert.
	 * 
	 * @param oldOwner alter Besitzer
	 * @param newOwner neuer Besitzer
	 */
	public void changeOwner(Player oldOwner, Player newOwner) {
		if (!oldOwner.getTickets().remove(this))
			throw new IllegalArgumentException("oldOwner is not the owner of this ticket.");
		
		if (!newOwner.getTickets().add(this))
			; // newUser does not want the ticket. -- if newUser already owns this ticket, set.add(this) throws an exception.
	}

}
