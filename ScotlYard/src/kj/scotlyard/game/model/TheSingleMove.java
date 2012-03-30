package kj.scotlyard.game.model;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.items.Item;

public class TheSingleMove extends DefaultMove {

	public TheSingleMove(Player player, int roundNumber, int moveNumber, StationVertex station, ConnectionEdge connection, Item item) {
		
		setPlayer(player);
		setRoundNumber(roundNumber);
		setMoveNumber(moveNumber);
		setMoveIndex(Move.NO_MOVE_INDEX);
		setStation(station);
		setConnection(connection);
		setItem(item);
	}
	
	
	
}
