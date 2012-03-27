package kj.scotlyard.game.model;

import java.util.List;

import org.apache.commons.attributes.Sealable;

import kj.scotlyard.game.model.items.Item;
import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;

public interface Move extends Sealable {
	
	// Round Number gibts immer
	
	int NO_MOVE_NUMBER = -1;
	
	int NO_MOVE_INDEX = -1;
	
	Player getPlayer();

	void setPlayer(Player player);

	int getRoundNumber();

	void setRoundNumber(int roundNumber);

	int getMoveNumber();
	
	void setMoveNumber(int moveNumber);
	
	int getMoveIndex();
	
	void setMoveIndex(int moveIndex);
	

	StationVertex getStation();

	void setStation(StationVertex station);
	
	ConnectionEdge getConnection();
	
	void setConnection(ConnectionEdge connection);


	// List/Set<Item> getItems();
	// oder
	Item getItem();
	
	void setItem(Item item);
	
	List<Move> getMoves();

}
