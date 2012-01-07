package kj.scotlyard.game.player;

import java.util.HashSet;
import java.util.Set;

import kj.scotlyard.game.card.Card;
import kj.scotlyard.game.card.DoubleMoveCard;

public class MrXPlayer extends Player {

	public MrXPlayer(String name) {
		super(name);
	}
	
	/**
	 * Liefert die Doppelzugkarten (Teilmenge der Karten)
	 * des MrXPlayers. Aenderungen im Ergebnis haben
	 * keine Auswirkung auf den Kartenbestand des
	 * Spielers! Benutzen Sie <tt>ticket.changeOwner</tt>.
	 * @return Menge der Doppelzugkarten; Aenderungen hierin
	 * haben keine Auswirkung auf den Kartenbestand!
	 */
	public Set<DoubleMoveCard> getDoubleMoveCards() {
		Set<DoubleMoveCard> tickets = new HashSet<>();
		for (Card c : getCards()) {
			if (c instanceof DoubleMoveCard)
				tickets.add((DoubleMoveCard) c);
		}
		return tickets;
	}
}
