/*
 * ScotlYard -- A software implementation of the Scotland Yard board game
 * Copyright (C) 2012  Jakob Schöttl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package kj.scotlyard.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.apache.log4j.Logger;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.TurnListener;
import kj.scotlyard.game.model.item.DoubleMoveCard;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.rules.MovePolicy;
import kj.scotlyard.game.rules.TheMovePolicy;
import kj.scotlyard.game.util.GameStateExtension;
import kj.scotlyard.game.util.MoveHelper;
import kj.scotlyard.game.util.MoveProducer;
import kj.scotlyard.game.util.SubMoves;

/**
 * This class defines the algorithm for "manual" move preparation.
 * This is the steps, the user make, to carry out a move:
 * <ol>
 * <li>Select the player</li>
 * <li>Select the next station</li>
 * <li>Select the ticket to use (callback)</li>
 * <li>Ready or go to 1. again for a multi move</li>
 * </ol>
 * When done, the turnkey move can be obtained via <code>getMove</code>.
 * 
 * The MovePreparer is (like a model in MVC) an Observable. It notify
 * it's observers
 * <ul>
 * <li>when the player is selected (arg = Player)</li>
 * <li>at step 4., this is a move <i>maybe</i> is ready (arg = Move)</li>
 * </ul>
 * 
 * @author jakob190590
 *
 */
public abstract class MovePreparer extends Observable {
	
	private static final Logger logger = Logger.getLogger(MovePreparer.class);
	
	private GameState gameState;
	
	private GameGraph gameGraph;
	
	// Feste Zugreihenfolge
	private boolean fixedTurnOrder;
	
	private Player player;
	
	private Map<Player, List<Move>> moves = new HashMap<>();
	
	private final TurnListener turnListener = new TurnListener() {
		@Override
		public void currentPlayerChanged(GameState gameState, Player oldPlayer, Player newPlayer) {
			if (gameState.getPlayers().indexOf(player) < gameState.getPlayers().indexOf(newPlayer) // wechseln wenn ausgewaehlter in runde schon dran war
					|| fixedTurnOrder // feste reihenfolge (bei "No Joint Moving")
					|| newPlayer instanceof MrXPlayer) { // oder wenn mrx an die reihe kommt
				if (!selectPlayer(newPlayer))
					logger.error("algo in selectPlayer laesst player der jetzt an die reihe kommt nicht zu!");
			}
		}
		@Override
		public void currentRoundChanged(GameState gameState, int oldRoundNumber, int newRoundNumber) { }
	};

	public MovePreparer(GameState gameState, GameGraph gameGraph) {
		setGameState(gameState);
		setGameGraph(gameGraph);
	}
	
	public MovePreparer() {
		this(null, null);
	}
	
	/**
	 * Get the moves in preparation for the specified
	 * player (<code>this.moves.get(player)</code>) and
	 * ensure that the result is not <code>null</code>.
	 * 
	 * To ensure that the result is not <code>null</code>,
	 * an <i>empty list</i> is put into the Map and returned.
	 * @param player
	 * @return the list of moves in preparation or an empty list
	 */
	private List<Move> getMoves(Player player) {
		List<Move> moves = this.moves.get(player);
		if (moves == null) {
			// Falls es doch mal höhere Multi Moves geben sollte: Capacity entsprechend erhöhen
			moves = new ArrayList<>(2);
			this.moves.put(player, moves);
		}
		return moves;
	}
	
	
	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		if (this.gameState != null) {
			this.gameState.removeTurnListener(turnListener);
		}
		this.gameState = gameState;
		if (gameState != null) {
			gameState.addTurnListener(turnListener);
		}
	}

	public GameGraph getGameGraph() {
		return gameGraph;
	}

	public void setGameGraph(GameGraph gameGraph) {
		this.gameGraph = gameGraph;
	}
	
	public boolean isFixedTurnOrder() {
		return fixedTurnOrder;
	}

	public void setFixedTurnOrder(boolean fixedTurnOrder) {
		this.fixedTurnOrder = fixedTurnOrder;
	}

	
	public void reset(Player player) {
		logger.debug("reset for " + player);
		moves.remove(player);
	}
	
	@Deprecated
	public void reset() {
		reset(gameState.getCurrentPlayer());
	}
	
	public void resetAll() {
		logger.debug("reset for all");
		moves.clear();
	}
	
	
	protected abstract void errorImpossibleNextStation(final StationVertex station, final Player player); // TODO reason: occupied or unreachable or ... ?

	/**
	 * Selects one ticket of the set and returns that ticket. The method may
	 * return <code>null</code> to cancel the move preparation. But this would 
	 * be not equivalent to <code>reset</code>: If there is a multi move
	 * in preparation, only the new sub move would be discarded.
	 * 
	 * This method will be called by <code>nextStation</code>. Implementations
	 * can ask the user to select one ticket.
	 * @param tickets a set of possible tickets
	 * @param player
	 * @return one of the passed tickets or <code>null</code>
	 */
	protected abstract Ticket selectTicket(final Set<Ticket> tickets, final Player player);

	protected abstract void errorSelectingPlayer(Player player);
	
	/**
	 * A template method for selecting a player.
	 * @param player
	 * @return <code>true</code> if the player is selected now
	 */
	public boolean selectPlayer(Player player) {
		logger.debug("try select player: " + player);
		
		boolean result = false;
		Player current = gameState.getCurrentPlayer();
		if (fixedTurnOrder && player != current) {
			logger.warn("selectPlayer: fixedTurnOrder ON; nur current player kann selected werden");
			errorSelectingPlayer(player);
		} else if (current instanceof MrXPlayer && player != current) {
			logger.warn("selectPlayer: jetzt ist NUR mrX dran! kein anderer kann selected werden");
			errorSelectingPlayer(player);
		} else if (gameState.getPlayers().indexOf(player) 
				< gameState.getPlayers().indexOf(current)) {
			logger.warn("selectPlayer: player war schon dran in currentRound! kann nicht selected werden");
			errorSelectingPlayer(player);
		} else {
			// Selection erfolgreich
			this.player = player;
			result = true;
			logger.debug("player selected");
		}
		setChanged(); // setChanged in jedem Fall: Falls z.B. MovePrep.Bar einen anderen Player auswaehlt, der nicht ausgewaehlt werden kann, muss die comboBox wieder zurueckgesetzt werden
		notifyObservers(this.player);
		return result;
	}
	
	// uebergibt im gegensatz zu nextStation gleich den kompletten zug! (verwendung bei suggest move von ai)
	// der move wird nicht im voraus geprueft, wie bei nextStation!
	public void nextMove(Move move) {
		Player p = move.getPlayer();
		List<Move> moves = getMoves(p);
		moves.clear();
		if (move.getMoves().isEmpty()) {
			// Single Move
			moves.add(move);
		} else {
			// Multi Move
			moves.addAll(move.getMoves());
		}
		setChanged();
		notifyObservers(getMove(move.getPlayer()));
	}
	
	public void nextStation(final StationVertex station, final Player player, DoubleMoveCard multiMoveCard) {
		// TODO egal wann aufgerufen wird: multiMoveCard != null  -> baseMove.setItem(multiMoveCard)
	}
	
	/**
	 * A template method for building a Move.
	 * This algorithm uses the <code>protected abstract</code> methods.
	 * This way the user interaction can be customized.
	 * @param station
	 * @param player
	 */
	public void nextStation(final StationVertex station, final Player player) {
		if (!selectPlayer(player)) return;
		
		logger.debug("next station");
		
		List<Move> moves = getMoves(player);
		
		Move lm = gameState.getLastMove(player); // last move
		int currentRoundNumber = gameState.getCurrentRoundNumber();
		StationVertex lastStation = lm.getStation();
		
		Move m = new DefaultMove();
		m.setStation(station);
		
		// Calculate all connections from current station to station
		Set<ConnectionEdge> connections = gameGraph.getGraph().getAllEdges(lastStation, station);		
		if (connections.isEmpty()) {
			logger.warn("nextStation: impossible station - no connections");
			errorImpossibleNextStation(station, player);
			return;
		}
		
		// next station pruefen
		// schon besetzt durch anderen Detective
		Iterator<Move> it = GameStateExtension.moveIterator(gameState, player, false, currentRoundNumber);
		while (it.hasNext()) {
			Move n = it.next();
			
			// Fuer den seltsamen Fall, dass schon Moves nach currentRound eingetragen sind
			if (n.getRoundNumber() > currentRoundNumber)
				break;
			
			// Station already occupied by foregoing detectives
			if (n.getPlayer() instanceof DetectivePlayer && n.getStation() == station) {
				logger.warn("nextStation: impossible station - already occupied by a foregoing detective (in current round)");
				errorImpossibleNextStation(station, player);
				return;
			}
		}
		// schon vorgemerkt durch anderen Detective
		for (DetectivePlayer d : gameState.getDetectives()) {
			// Nachfolgende Detectives sind nicht relevant
			if (d == player)
				break;
			
			// Station already prepared by foregoing detectives
			Move n = getMove(d);
			if (n != null && n.getStation() == station) {
				logger.warn("nextStation: impossible station - already prepared by a foregoing detective (in current round)");
				errorImpossibleNextStation(station, player);
				return;
			}
		}
		
		// Alle Tickets raussuchen, die fuer die Connections gueltig sind
		Set<Ticket> tickets = new HashSet<>();
		Set<Item> allItems = gameState.getItems(player);
		MovePolicy mp = new TheMovePolicy();
		for (ConnectionEdge c : connections) {
			for (Item i : allItems) {
				if (i instanceof Ticket && mp.isTicketValidForConnection((Ticket) i, c)) {
					tickets.add((Ticket) i);
				}
			}			
		}
		// Tickets wieder rausnehmen, die bei dem Zug, der 
		// aktuell vorbereitet wird, schon benutzt sind!
		for (Move n : moves) {
			tickets.remove((Ticket) n.getItem());
		}
		
		Ticket ticket = selectTicket(tickets, player);
		
		if (ticket != null) {		
			// D.h. nicht abgebrochen
			logger.debug("ticket selected");
			ConnectionEdge conn = MoveHelper.suggestConnection(lastStation, station, ticket);
			m.setConnection(conn);
			m.setItem(ticket);
			
			// Publish m in moves
			moves.add(m);
			
			setChanged();
			notifyObservers(getMove(player));
		}
	}
	
	@Deprecated
	public void nextStation(final StationVertex station) {
		nextStation(station, gameState.getCurrentPlayer());
	}
	
	public Player getPlayer() {
		return player;
	}
		
	public Move getMove(Player player) {
		logger.debug("try to get the turnkey move for: " + player);
		List<Move> moves = getMoves(player);	
		Move result = null;
		logger.debug("prepared move exists");
		
		// Move und Round number nur setzen, wenn player == currentPlayer
		int roundNumber = 0;
		int moveNumber = 0;		
		if (player == gameState.getCurrentPlayer()) {
			roundNumber = gameState.getCurrentRoundNumber();
			moveNumber = GameStateExtension.getLastMoveFlat(gameState, player).getMoveNumber() + 1;
		}
		
		int nMoves = moves.size();
		if (nMoves == 1) {
			// Single Move
			Move m = moves.get(0);
			result = MoveProducer.createSingleMove(player, roundNumber, moveNumber,
					m.getStation(), m.getConnection(), (Ticket) m.getItem());
		} else if (nMoves > 1) {
			// Multi Move
			Item multiMoveCard = null;
			if (nMoves == 2) {
				// Automatisch die DoubleMoveCard verwenden (wenn vorhanden)
				multiMoveCard = GameStateExtension.getItem(gameState, player, DoubleMoveCard.class);
			} else {
				logger.error("multi moves higher than double moves are not yet supported by GUI/MovePreparer: don't know which Item (multimove card) to choose.");
			}
			SubMoves sms = new SubMoves();
			for (Move m : moves) {
				sms.add(m.getStation(), m.getConnection(), (Ticket) m.getItem());
			}
			result = MoveProducer.createMultiMove(player, roundNumber, moveNumber, 
					(DoubleMoveCard) multiMoveCard, sms);
		}

		return result;
	}
	
	@Deprecated
	public Move getMove() {
		return getMove(gameState.getCurrentPlayer());
	}
}
