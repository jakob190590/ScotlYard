package kj.scotlyard.game.model;

import java.util.List;

import org.apache.commons.attributes.Sealable;

import kj.scotlyard.game.items.Item;
import kj.scotlyard.game.graph.StationVertex;

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


	List<Item> getItems();
	
	List<Move> getMoves();

}
