package kj.scotlyard.game;

import kj.scotlyard.game.card.TicketCard;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.player.Player;

/**
 * Schnittstelle des Spielzuges eines Spielers.
 * 
 * @author jakob190590
 *
 */
public interface Move implements Sealable {
	
	Player getPlayer();

	void setPlayer(Player player);

	int getRound();

	void setRound(int round);
/*
	int getMoveNumber();
	
	void setMoveNumber(int moveNumber);
	
	int getMoveIndex();
	
	void setMoveIndex(int moveIndex);
*/	

	StationVertex getStation();

	void setStation(StationVertex station);


	Set<Item> getItems();
	
	List<Move> getMoves();

}
