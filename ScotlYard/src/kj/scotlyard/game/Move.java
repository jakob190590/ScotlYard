package kj.scotlyard.game;

import java.util.List;
import java.util.Set;

import org.apache.commons.attributes.Sealable;

import kj.scotlyard.game.items.Item;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.player.PlayingPiece;

public interface Move extends Sealable {
	
	PlayingPiece getPlayer();

	void setPlayer(PlayingPiece player);

	int getRound();

	void setRound(int round);

	int getMoveNumber();
	
	void setMoveNumber(int moveNumber);
	
	int getMoveIndex();
	
	void setMoveIndex(int moveIndex);
	

	StationVertex getStation();

	void setStation(StationVertex station);


	Set<Item> getItems();
	
	List<Move> getMoves();

}
