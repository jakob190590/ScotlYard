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
import kj.scotlyard.game.model.items.DoubleMoveCard;
import kj.scotlyard.game.model.items.Item;
import kj.scotlyard.game.model.items.TaxiTicket;
import kj.scotlyard.game.model.items.Ticket;
import kj.scotlyard.game.model.items.UndergroundTicket;

public class TheMovePolicy implements MovePolicy {

	private void throwIllegalMoveException(String s) {
		throw new IllegalMoveException(s);
	}
	
	private void checkSingleMove(GameState gameState, GameGraph gameGraph, Move move)
			throws IllegalMoveException {
		
		if (!(move.getItem() instanceof Ticket)) {
			throwIllegalMoveException("You must provide a ticket for this move.");
		}
		
		// TODO passt station?
		// TODO liegt connection zwischen stations? 
		
		Ticket t = (Ticket) move.getItem();
		if (!isTicketValidForConnection(t, move.getConnection())) {
			throwIllegalMoveException("Your ticket is not valid for your connection.");
		}			
		if (!gameState.getItems(move.getPlayer()).contains(t)) {
			throwIllegalMoveException("The ticket you are providing is not your's. You must have stolen it.");
		}
	}
	
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
		
		
		// Allgemein
		boolean subMoves = !move.getMoves().isEmpty(); // there are sub moves
		
		if (move.getPlayer() != gameState.getCurrentPlayer()) {
			throwIllegalMoveException("It is not your player's turn.");
		}
		
		if (move.getRoundNumber() != gameState.getCurrentRoundNumber()) {
			throwIllegalMoveException("The specified round number is not the current one.");
		}
		
		
		
		if (gameState.getCurrentRoundNumber() == GameState.INITIAL_ROUND_NUMBER) {
			// Initial Move
			if (move.getItem() != null) {
				throwIllegalMoveException("You cannot attach an item in the initial move.");
			}
			if (subMoves) {
				throwIllegalMoveException("You cannot carry out a multi move in the initial round.");
			}
			if (move.getMoveNumber() != GameState.INITIAL_MOVE_NUMBER) {
				throwIllegalMoveException("The specified move number must be the initial move number.");
			}	
			if (move.getConnection() != null) {
				throwIllegalMoveException("You cannot attach a connection in the initial move.");
			}
			// TODO Station ueberpruefen
			
		} else if (subMoves) {
			// Multi Move
			
			if (move.getPlayer() instanceof DetectivePlayer) {
				throwIllegalMoveException("Only MrX can carry out a multi move.");
			}
			if (move.getMoveNumber() != Move.NO_MOVE_NUMBER) {
				throwIllegalMoveException("A multi move cannot have a move number. Use Move.NO_MOVE_NUMBER.");
			}
			if (move.getMoveIndex() != Move.NO_MOVE_INDEX) {
				throwIllegalMoveException("A multi move cannot have a move index. Use Move.NO_MOVE_INDEX.");
			}
			
			if (move.getMoves().size() != 2) {
				throwIllegalMoveException("This is neither a single and nor a double move. Think about it.");
			}
			if (!(move.getItem() instanceof DoubleMoveCard)) {
				throwIllegalMoveException("You must provide a double move card for this move.");
			}
			if (!gameState.getItems(move.getPlayer()).contains(move.getItem())) {
				throwIllegalMoveException("The card you are providing is not your's. You must have stolen it.");
			}
			
			for (Move m : move.getMoves()) {
				checkSingleMove(gameState, gameGraph, m);
			}
			if (move.getStation() != move.getMoves().get(1).getStation()) {
				throwIllegalMoveException("The station you are traveling to must be consistent within this multi move. (The station of this and the last sub move must be the same.)");
			}
			
		} else {
			// Single Move
			
			checkSingleMove(gameState, gameGraph, move);
		}

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
