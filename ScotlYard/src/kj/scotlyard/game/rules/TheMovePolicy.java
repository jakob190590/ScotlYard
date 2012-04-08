package kj.scotlyard.game.rules;

import java.util.Set;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
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
		// ist aber kritisch, weil dadurch der move vllt in die falschen "haende" geraten koennt
		if (doRaiseException) {
			throw new IllegalMoveException(message);
		}
	}
	
	private void checkSingleMove(GameState gameState, GameGraph gameGraph, Move move, Move previousMove)
			throws IllegalMoveException {
		
		throwIllegalMove(move.getMoveNumber() != (previousMove.getMoveNumber() + 1), 
				"The move number must be the previous move number plus one.");
	
		throwIllegalMove(move.getStation() == null, 
				"You must specify the target station.");
		
		// TODO korbi ?
		// - ist move.station direkter nachbar von previousMove.station ?
		// - liegt move.connection dazwischen ?
		
		throwIllegalMove(!(move.getItem() instanceof Ticket), 
				"You must provide a ticket for this move.");
		
		Ticket t = (Ticket) move.getItem();
		
		throwIllegalMove(!isTicketValidForConnection(t, move.getConnection()), 
				"Your ticket is not valid for your connection.");
		
		throwIllegalMove(!gameState.getItems(move.getPlayer()).contains(t), 
				"The ticket you are providing is not your's. You must have stolen it.");
		
	}

	@Override
	public boolean canMove(GameState gameState, GameGraph gameGraph,
			Player player) {

		// fuer jede anliegende edge pruefen, ob ticket da is
		// wenn ticket da is, pruefen, ob benachbarte station frei is (kein anderer detektiv)
		
		// TODO implement, korbi?
		
		Move lastMove = gameState.getLastMove(player);
		StationVertex currentStation = lastMove.getStation();
		
		Set<Item> items = gameState.getItems(player);
		Set<StationVertex> detectivePositions = new GameStateExtension(gameState).getDetectivePositions(lastMove.getRoundNumber());
		
		for (ConnectionEdge connection : currentStation.getEdges()) {
			
			boolean ticketFound = false;
			for (Item item : items) {
				if (item instanceof Ticket && isTicketValidForConnection((Ticket) item, connection)) {
					ticketFound = true;
					break;
				}
			}
			
			if (ticketFound && !detectivePositions.contains(connection.getOther(currentStation))) {
				return true;
			}
		}

		return false;
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
		Move previous = new GameStateExtension(gameState).getLastMoveFlat(move.getPlayer());

		throwIllegalMove(!gameState.getPlayers().contains(move.getPlayer()), 
				"The specified player is not part of this game.");
		
		throwIllegalMove(move.getPlayer() != gameState.getCurrentPlayer(),  
				"It is not your player's turn.");
	
		throwIllegalMove(move.getRoundNumber() != gameState.getCurrentRoundNumber(), 
				"The specified round number is not the current one.");
		
		throwIllegalMove(move.getMoveIndex() != Move.NO_MOVE_INDEX,
				"This move must not have a move index. Only sub moves may and must " +
				"have one. Use Move.NO_MOVE_INDEX here.");
		
		
		if (previous == null) {
			// Initial Move
			
			throwIllegalMove(move.getMoveNumber() != GameState.INITIAL_MOVE_NUMBER, 
					"The specified move number must be the initial move number.");
			
			// station wird auch in checkSingleMove ueberprueft
			throwIllegalMove(move.getStation() == null, 
					"You must specify the initial station.");
			
			// TODO Station ueberpruefen
			// - dass da kein anderer
			// - und element von Graph ist
			
			throwIllegalMove(move.getConnection() != null, 
					"You cannot attach a connection to the initial move.");
			
			throwIllegalMove(move.getItem() != null, 
					"You cannot attach an item in the initial move.");
			
			throwIllegalMove(subMoves, 
					"You cannot carry out a multi move in the initial round.");
			
			
		} else if (subMoves) {
			// Multi Move
		
			throwIllegalMove(move.getPlayer() != gameState.getMrX(),
					"Only MrX can carry out a multi move.");
			
			throwIllegalMove(move.getMoveNumber() != Move.NO_MOVE_NUMBER,
					"A multi move cannot have a move number, since it's sub moves " +
					"already have one. Use Move.NO_MOVE_NUMBER.");
			
			throwIllegalMove(move.getStation() == null,
					"You must specify the final station.");
			
			throwIllegalMove(move.getConnection() != null, 
					"You cannot attach a connection to a multi move.");
			
			throwIllegalMove(!(move.getItem() instanceof DoubleMoveCard), 
					"You must provide a double move card for this move.");
			
			throwIllegalMove(move.getMoves().size() != 2, 
					"Multi moves have only one manifestation: a double move. " +
					"You have to attach exactly two sub moves.");
			
			throwIllegalMove(!gameState.getItems(move.getPlayer()).contains(move.getItem()), 
					"The card you are providing is not your's. You must have stolen it.");
			
			int i = 0;
			for (Move m : move.getMoves()) {
				
				throwIllegalMove(m.getPlayer() != move.getPlayer(), 
						"The player must be consistent within the multi move. " +
						"(The player of at least one sub move differs from the one of the base move.)");
				
				throwIllegalMove(m.getRoundNumber() != move.getRoundNumber(), 
						"The round number must be consistent within the multi move. " +
						"(The round number of at least one sub move differs from the one of the base move.)");
				
				throwIllegalMove(m.getMoveIndex() != i, 
						"The move index of a sub move is " + m.getMoveIndex() + " but must be: " + i);
				
				throwIllegalMove(!m.getMoves().isEmpty(), 
						"A sub move must not have further sub moves."); // further or more ?
				
				checkSingleMove(gameState, gameGraph, m, previous);
				
				previous = m;
				i++;
			}
			
			throwIllegalMove(move.getStation() != move.getMoves().get(1).getStation(),
					"The station you are traveling to must be consistent within this multi move. " +
					"(The station of the base move and it's last sub move must be the same.)");
			
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
