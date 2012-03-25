package kj.scotlyard.game.rules;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.items.Item;
import kj.scotlyard.game.model.items.Ticket;

public interface MovePolicy {	
	
	boolean isTicketValidForConnection(Ticket ticket, ConnectionEdge connection);
	
	void checkMove(GameState gameState, GameGraph gameGraph, Move move) throws IllegalMoveException;
	
	Player getNextItemOwner(GameState gameState, Move move, Item item); // Parameter Item, falls es mal mehrere Items in einem Move gibt.
	
}
