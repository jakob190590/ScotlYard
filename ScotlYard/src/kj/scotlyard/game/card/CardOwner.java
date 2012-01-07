package kj.scotlyard.game.card;

import java.util.Set;

/**
 * CardOwner sind normalerweise Spieler, es kann aber
 * auch eine Bank oder eine Ablage fuer ausgespielte
 * Karten geben, die dann auch dieses Interface
 * implementieren und somit CardOwner sind.
 * So koennen Karten ganz allgemein weitergegeben oder
 * getauscht werden ...
 * @author jakob190590
 *
 */
public interface CardOwner {
	
	/**
	 * Spielkarten des Owner. Die Set-Implementation 
	 * sollte keine doppelten Karten und keine 
	 * null-Werte zulassen.
	 * @return Menge der Spielkarten
	 */
	Set<Card> getCards();
	
}
