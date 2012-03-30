package kj.scotlyard.game.model;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.items.Ticket;

public class TheSubMove extends DefaultMove {

	public TheSubMove(StationVertex station, ConnectionEdge connection, Ticket item) {
		
		setPlayer(null);
		setRoundNumber(0);
		setMoveNumber(Move.NO_MOVE_NUMBER);
		setMoveIndex(Move.NO_MOVE_INDEX);
		setStation(station);
		setConnection(connection);
		setItem(item);
	}
	
	
	
}
