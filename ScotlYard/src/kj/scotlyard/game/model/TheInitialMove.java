package kj.scotlyard.game.model;

import kj.scotlyard.game.graph.StationVertex;

public class TheInitialMove extends DefaultMove {

	public TheInitialMove(Player player, StationVertex station) {
		
		setPlayer(player);
		setRoundNumber(GameState.INITIAL_ROUND_NUMBER);
		setMoveNumber(GameState.INITIAL_MOVE_NUMBER);
		setMoveIndex(Move.NO_MOVE_INDEX);
		setStation(station);
		setConnection(null);
		setItem(null);		
	} 
	
}
