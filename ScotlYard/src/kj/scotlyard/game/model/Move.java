package kj.scotlyard.game.model;

import java.util.List;

import org.apache.commons.attributes.Sealable;

import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.graph.Connection;
import kj.scotlyard.game.graph.Station;

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
	

	Station getStation();

	void setStation(Station station);
	
	Connection getConnection();
	
	void setConnection(Connection connection);


	// List/Set<Item> getItems();
	// oder
	Item getItem();
	
	void setItem(Item item);
	
	List<Move> getMoves();

}
