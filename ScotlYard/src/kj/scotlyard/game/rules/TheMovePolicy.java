package kj.scotlyard.game.rules;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.items.BlackTicket;
import kj.scotlyard.game.model.items.BusTicket;
import kj.scotlyard.game.model.items.Item;
import kj.scotlyard.game.model.items.TaxiTicket;
import kj.scotlyard.game.model.items.Ticket;
import kj.scotlyard.game.model.items.UndergroundTicket;

public class TheMovePolicy implements MovePolicy {

	@Override
	public boolean isTicketValidForConnection(Ticket ticket,
			ConnectionEdge connection) {
		
		if (ticket instanceof TaxiTicket && connection instanceof TaxiConnection) {
			return true;
		}
		
		if (ticket instanceof BusTicket && connection instanceof BusConnection) {
			return true;
		}
		
		if (ticket instanceof UndergroundTicket && connection instanceof UndergroundConnection) {
			return true;
		}
		
		if (ticket instanceof BlackTicket) {
			return true;
		}
		
		return false;
	}

	@Override
	public void checkMove(GameState gameState, GameGraph gameGraph, Move move)
			throws IllegalMoveException {
		// TODO Auto-generated method stub

	}

	@Override
	public Player getNextItemOwner(GameState gameState, Move move, Item item) {
		if (move.getPlayer() instanceof DetectivePlayer) {
			return gameState.getMrX();
		}
		
		// else: MrX' Move
		return null;
	}

}
