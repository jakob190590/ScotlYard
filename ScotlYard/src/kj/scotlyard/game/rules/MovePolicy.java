package kj.scotlyard.game.rules;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;

public interface MovePolicy {	
	
	void checkMove(GameState gameState, GameGraph gameGraph, Move move) throws IllegalMoveException;
	
	Player getNextItemOwner(GameState gameState, Move move);
	
	boolean isTicketValidForConnection(Ticket ticket, ConnectionEdge connection);
	
}
