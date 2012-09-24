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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.apache.log4j.Logger;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
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
	
	private Player player;
	
	private Map<Player, Move> moves = new HashMap<>();
	
	private final turnListener = new TurnListener() {
		void currentPlayerChanged(GameState gameState, Player oldPlayer, Player newPlayer) {
			if (gameState.getPlayers().indexOf(player) < gameState.getPlayers().indexOf(newPlayer)) {
				if (!selectPlayer(newPlayer))
					logger.error("algo in selectPlayer laesst player der jetzt an die reihe kommt nicht zu!");
			}
		}
		void currentRoundChanged(GameState gameState, int oldRoundNumber, int newRoundNumber) { }
	}

	public MovePreparer(GameState gameState, GameGraph gameGraph) {
		setGameState(gameState);
		setGameGraph(gameGraph);
	}
	
	public MovePreparer() {
		this(null, null);
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
		if (current instanceof MrXPlayer && player != current) {
			logger.warn("selectPlayer: jetzt ist NUR mrX dran! kein anderer kann selected werden")
			errorSelectingPlayer(player);
		} else if (gameState.getPlayers().indexOf(player) 
				< gameState.getPlayers().indexOf(current)) {
			logger.warn("selectPlayer: player war schon dran in currentRound!")
			errorSelectingPlayer(player);
		} else {			
			if (this.player != player) {
				this.player = player;
				setChanged();
			}
			result = true;
			logger.debug("player selected");
		}
		notifyObservers(this.player);
		return result;
	}
	
	// uebergibt im gegensatz zu nextStation gleich den kompletten zug! (verwendung bei suggest move von ai)
	// der move wird nicht im voraus geprueft, wie bei nextStation!
	public void nextMove(Move move) {
		moves.put(move.getPlayer(), move);
		setChanged();
		notifyObservers(getMove(move.getPlayer()));
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
		
		Move move = moves.get(player);
		
		Move lm = gameState.getLastMove(player); // last move
		StationVertex lastStation = lm.getStation();
		
		Move m = new DefaultMove();
		m.setStation(station);
		
		// Calculate all connections from current station to station
		Set<ConnectionEdge> connections = gameGraph.getGraph().getAllEdges(lastStation, station);		
		if (connections.isEmpty()) {
			errorImpossibleNextStation(station, player);
			return;
		}
		
		// TODO next station muss auch noch anderweitig geprueft werden (besetzt, kein ticket, ...?)
		
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
		
		Ticket ticket = selectTicket(tickets, player);
		
		if (ticket != null) {		
			// D.h. nicht abgebrochen
			logger.debug("ticket selected");
			ConnectionEdge conn = MoveHelper.suggestConnection(lastStation, station, ticket);
			m.setConnection(conn);
			m.setItem(ticket);
			
			// Publish m as/in move
			if (move == null) {
				move = m;
			} else {				
				if (move.getMoves().isEmpty()) {
					// move ist noch kein Multi Move
					// -> move neuem Move als Sub Move adden
					Move n = new DefaultMove();
					n.getMoves().add(move);
					move = n;
				}
				move.getMoves().add(m);
			}
			
			moves.put(player, move);
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
		logger.debug("try get move (turnkey)");
		Move move = moves.get(player);
		
		Move result = null;
		if (move != null) {
			logger.debug("prepared move exists");
			
			// Move und Round number nur setzen, wenn player == currentPlayer
			int roundNumber = 0;
			int moveNumber = 0;		
			if (player == gameState.getCurrentPlayer()) {
				roundNumber = gameState.getCurrentRoundNumber();
				moveNumber = gameState.getMoves().get(GameState.LAST_MOVE);// TODO ?? hat doch funktioniert... getLastMove(player).getMoveNumber() + 1; // Exception abfangen? eher ned, den fall sollts ja nicht geben
			}
			
			if (move.getMoves().isEmpty()) {
				// Single Move
				result = MoveProducer.createSingleMove(player, roundNumber, moveNumber,
						move.getStation(), move.getConnection(), (Ticket) move.getItem());
			} else {
				// Multi Move
				GameStateExtension gsx = new GameStateExtension(gameState);
				DoubleMoveCard doubleMoveCard = (DoubleMoveCard) gsx.getItem(player, DoubleMoveCard.class);
				SubMoves sms = new SubMoves();
				for (Move m : move.getMoves()) {
					sms.add(m.getStation(), m.getConnection(), (Ticket) m.getItem());
				}
				result = MoveProducer.createMultiMove(player, roundNumber, moveNumber, 
						doubleMoveCard, sms);
			}
		}
		return result;
	}
	
	@Deprecated
	public Move getMove() {
		return getMove(gameState.getCurrentPlayer());
	}
}
