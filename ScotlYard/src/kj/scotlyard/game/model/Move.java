package kj.scotlyard.game.model;

import java.util.List;

import org.apache.commons.attributes.Sealable;

import kj.scotlyard.game.model.items.Item;
import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;

public interface Move extends Sealable {
	
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


	//List<Item> getItems();
	// oder
	Item getItem();
	
	List<Move> getMoves();

}
