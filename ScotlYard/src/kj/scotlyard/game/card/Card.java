package kj.scotlyard.game.card;

/**
 * Repraesentiert eine Karte im Spiel.
 * @author jakob190590
 *
 */
public abstract class Card {
	
	/**
	 * Aendert den Besitzer des Tickets. In Scotland Yard koennen Tickets
	 * eigentlich nur von den Detektiven zum Mr. X wandern. Durch diese
	 * allgemeine Methode bin ich dafuer aber unabhaengiger von anderen
	 * Objekten wie MrX oder Game, das den MrX referenziert.
	 * 
	 * @param oldOwner alter Besitzer
	 * @param newOwner neuer Besitzer
	 */
	public void changeOwner(CardOwner oldOwner, CardOwner newOwner) {
		if (!oldOwner.getCards().remove(this))
			throw new IllegalArgumentException("oldOwner is not the owner of this ticket.");
		
		if (!newOwner.getCards().add(this))
			; // newOwner does not want the ticket. -- if newUser already owns this ticket, set.add(this) throws an exception.
	}
	
}
