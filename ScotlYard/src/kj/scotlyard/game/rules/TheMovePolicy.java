package kj.scotlyard.game.rules;

import kj.scotlyard.game.graph.Connection;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.BlackTicket;
import kj.scotlyard.game.model.item.BusTicket;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.TaxiTicket;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.model.item.UndergroundTicket;
import kj.scotlyard.game.util.GameStateExtension;

public class TheMovePolicy implements MovePolicy {

	private void throwIllegalMove(boolean doRaiseException, String message) {
		// TODO evtl move als argument, und hinter message anhaengen.
		if (doRaiseException) {
			throw new IllegalMoveException(message);
		}
	}
	
	private void checkSingleMove(GameState gameState, GameGraph gameGraph, Move move, Move previousMove)
			throws IllegalMoveException {
		
		throwIllegalMove(move.getMoveNumber() != (previousMove.getMoveNumber() + 1), 
				"The move number must be the previous move number plus one.");
	
		throwIllegalMove(!(move.getItem() instanceof Ticket), 
				"You must provide a ticket for this move.");
	
		throwIllegalMove(move.getStation() == null, 
				"You must specify the target station.");
		
		// TODO korbi ?
		// - ist move.station direkter nachbar von previousMove.station ?
		// - liegt move.connection dazwischen ?
		
		Ticket t = (Ticket) move.getItem();
		
		throwIllegalMove(!isTicketValidForConnection(t, move.getConnection()), 
				"Your ticket is not valid for your connection.");
		
		throwIllegalMove(!gameState.getItems(move.getPlayer()).contains(t), 
				"The ticket you are providing is not your's. You must have stolen it.");
	}
	
	@Override
	public boolean isTicketValidForConnection(Ticket ticket,
			Connection connection) {
		
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
		Move previous = new GameStateExtension(gameState).getLastMoveFlat(move.getPlayer());
		
		throwIllegalMove(move.getPlayer() != gameState.getCurrentPlayer(), 
				"It is not your player's turn.");
	
		throwIllegalMove(move.getRoundNumber() != gameState.getCurrentRoundNumber(), 
				"The specified round number is not the current one.");
		
		throwIllegalMove(move.getMoveIndex() != Move.NO_MOVE_INDEX,
				"This move must not have a move index. Only sub moves may and must have one. Use Move.NO_MOVE_INDEX here.");
		
		
		if (previous == null) {
			// Initial Move
			throwIllegalMove(move.getStation() == null, 
					"You must specify the initial station.");
			
			throwIllegalMove(move.getItem() != null, 
					"You cannot attach an item in the initial move.");
			
			throwIllegalMove(subMoves, 
					"You cannot carry out a multi move in the initial round.");
			
			throwIllegalMove(move.getMoveNumber() != GameState.INITIAL_MOVE_NUMBER, 
					"The specified move number must be the initial move number.");
			
			throwIllegalMove(move.getConnection() != null, 
					"You cannot attach a connection in the initial move.");
			
			// TODO Station ueberpruefen
			
		} else if (subMoves) {
			// Multi Move
		
			throwIllegalMove(move.getPlayer() instanceof DetectivePlayer,
					"Only MrX can carry out a multi move.");
			
			throwIllegalMove(move.getMoveNumber() != Move.NO_MOVE_NUMBER,
					"A multi move cannot have a move number. Use Move.NO_MOVE_NUMBER.");
			
			throwIllegalMove(move.getStation() == null,
					"You must specify the final station.");
			
			throwIllegalMove(move.getMoves().size() != 2, 
					"This is neither a single/normal and nor a double move. Think about it.");
			
			throwIllegalMove(!(move.getItem() instanceof DoubleMoveCard), 
					"You must provide a double move card for this move.");
			
			throwIllegalMove(!gameState.getItems(move.getPlayer()).contains(move.getItem()), 
					"The card you are providing is not your's. You must have stolen it.");
			
			int i = 0;
			for (Move m : move.getMoves()) {
				
				throwIllegalMove(m.getMoveIndex() != i, 
						"The move index of a sub move is " + m.getMoveIndex() + " but must be: " + i);
				
				checkSingleMove(gameState, gameGraph, m, previous);
				
				previous = m;
				i++;
			}
			throwIllegalMove(move.getStation() != move.getMoves().get(1).getStation(),
					"The station you are traveling to must be consistent within this multi move. " +
					"(The station of this and the last sub move must be the same.)");
			
		} else {
			// Single Move
			
			checkSingleMove(gameState, gameGraph, move, previous);
		}

	}

	@Override
	public Player getNextItemOwner(GameState gameState, Move move, Item item) {
		
		// Grundlegende Gueltigkeitstests
		boolean itemInMove = false;
		for (Move m : new GameStateExtension(gameState).flattenMove(move, false)) {
			if (m.getItem() == item) {
				itemInMove = true;
				break;
			}
		}
		if (!itemInMove) {
			throw new IllegalMoveException("The specified item is not attached to the specified move.");
		}
		
		
		if (item != null && move.getPlayer() instanceof DetectivePlayer) {
			return gameState.getMrX();
		}
		
		// else: item == null or MrX' Move
		return null;
	}

}
