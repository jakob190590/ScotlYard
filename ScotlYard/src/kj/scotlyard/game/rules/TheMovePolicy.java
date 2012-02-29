package kj.scotlyard.game.rules;

import java.util.Collection;

import kj.scotlyard.game.Game;
import kj.scotlyard.game.Move;
import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.items.Card;
import kj.scotlyard.game.items.DoubleMoveCard;
import kj.scotlyard.game.player.MrXPiece;
import kj.scotlyard.game.player.PlayingPiece;

/**
 * The official Scotland Yard rules are implemented by
 * this MovePolicy.
 * 
 * @author jakob190590
 *
 */
public class TheMovePolicy implements MovePolicy {
	
	private Game game;
	
	public TheMovePolicy(Game game) {
		this.game = game;
	}

	// TODO exception/exception erzeugung
	// TODO genauen grund angeben: welcher player, welcher move, welche station, ... (toString())
	// TODO Test fuer TheMovePolicy schreiben!
	
	@Override
	public void checkMove(Move move) {
		if (move == null)
			throw new NullPointerException("Move cannot be null.");
		
		// Player merken, weil der bei Mehrfachzuegen nicht abweichen darf
		PlayingPiece player = move.getPlayer();
		
		// Spieler muss zumindest im ersten Move der Kette angegeben sein
		if (player == null) 
			throw new NullPointerException("Player cannot be null (at least not in the first move in the chain).");
		
		// Spieler is gar ned an der Reihe
		if (player != game.getCurrentPlayer())
			throw new IllegalArgumentException("It is not specified player's turn.");
		
		// Spieler scheint erstmal gueltig -- sein letzter Standort ist:
		StationVertex station = game.getLastMoveOf(player).getNextStation(); // TODO wie es bis jetzt aussieht, ist das nicht der letzte Zug, wenn's ein Mehrfachzug ist!
		
		// Multizug mit 2 Zuegen
		if (move.getNextMove() != null) {
			
			// das kann hoechstens MrX
			if (!(player instanceof MrXPiece))
				throw new IllegalArgumentException("Only Mr. X can do a multimove with two moves.");
			
			// mehr als 2 moves gehn gar nicht
			if (move.getNextMove().getNextMove() != null)
				throw new IllegalArgumentException("No player can do a multimove with more than two moves.");
			
			// ok, hat MrX denn eine Doppelzugkarte?
			boolean hasDoubleMoveCard = false; 
			for (Card c : player.getCards()) {
				if (c instanceof DoubleMoveCard) {
					hasDoubleMoveCard = true;
					break;
				}
			}
			
			// MrX hat keine Doppelzugkarte mehr
			if (!hasDoubleMoveCard)  
				throw new IllegalArgumentException("Mr. X does not have a double move card.");
			
		}
		
		// Verkettete Move-Liste abklappern
		while (move != null) {

			// Spieler angegeben, aber unterscheidet sich von dem des ersten Zugs
			if ((move.getPlayer() != null) && (move.getPlayer() != player))
				throw new IllegalAccessError("Player differs within one multimove.");
				
			// Die Station wo er hinwill MUSS gegeben sein, sonst macht der Zug keinen Sinn
			if (move.getNextStation() == null)
				throw new NullPointerException("Next station cannot be null.");
			
			// Gehoert das eingesetzte Ticket ueberhaupt dem Spieler?
			if (!player.getCards().contains(move.getTicket()))
				throw new IllegalArgumentException("The ticket does not belong to the player (the player has stolen it).");
			
			// Die Station wo er hinwill ist gar nicht mit einem Zug erreichbar
			if (!station.getNeighbors().contains(move.getNextStation()))
				throw new IllegalArgumentException("Next station is not reachable within this one move.");
			
			// Testen, ob Ticket fuer mindestens eine Verbindung zwischen Start und Ziel gueltig ist:
			Collection<ConnectionEdge> connections = station.getEdgesBetweenThisAnd(move.getNextStation());
			boolean ticketIsValid = false;
			for (ConnectionEdge ce : connections) {
				if (move.getTicket().isValidFor(ce)) {
					ticketIsValid = true;
					break;
				}
			}

			if (!ticketIsValid) // Ungueltiges Ticket
				throw new IllegalArgumentException("The given ticket is not valid for this connection.");
			
			// alle anderen spieler ...
			for (PlayingPiece p : game.getPlayers()) {
				if (p != player) {
					if (game.getPositionOf(p) == move.getNextStation())
						throw new IllegalArgumentException("Next station is already occupied.");
				}
			}
			
			// TODO fehlt noch was?
			
			// hier angekommen, scheint der Zug soweit gueltig zu sein;
			// falles es weitergeht (Mehrfachzug) neue Station merken:
			station = move.getNextStation();
			
			// Weiter in verketteter Move-Liste
			move = move.getNextMove();
		}	
		
	}
	
	public String toString() {
		return "The official Scotland Yard rules MovePolicy implementation";
	}

}
