package kj.scotlyard.game.items;

import kj.scotlyard.game.graph.ConnectionEdge;

/**
 * Diese Klasse repraesentiert ein Ticket, mit dem der Spieler
 * von einer Station zur naechsten reisen kann.
 * 
 * Zu den Tickets zaehlen wirklich nur Fahrkarten, nicht die Karten
 * fuer die Doppelzuege des Mr. X oder sonstiges.
 * 
 * @author jakob190590
 *
 */
public abstract class Ticket extends Item {
	
	/**
	 * Prueft, ob dieses Ticket fuer die angegebene Verbindung
	 * gueltig ist. Das Ticket sollte nur gueltig sein koennen,
	 * wenn es noch nicht entwertet wurde.
	 * @param connection die Verbindung
	 * @return <tt>true</tt>, wenn das Ticket gueltig ist
	 */
	public abstract boolean isValidFor(ConnectionEdge connection);
	
}
