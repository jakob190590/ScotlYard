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

package kj.scotlyard.game.ai;

import java.util.HashSet;
import java.util.Set;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MoveListener;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.TurnListener;

public abstract class AbstractAi implements Ai {
	
	private GameState gameState;
	
	private GameGraph gameGraph;
	
	private boolean ready;
	
	private int timeLeft;

	private int timeLimit;
	
	private boolean decideNowFlag;

	private Set<AiListener> listeners = new HashSet<>();
	
	private final MoveListener moveListener = new MoveListener() {
		@Override
		public void movesCleard(GameState gameState) {
		}
		@Override
		public void moveUndone(GameState gameState, Move move) {
			assert gameState == AbstractAi.this.gameState;
			AbstractAi.this.moveUndone(move);
		}
		
		@Override
		public void moveDone(GameState gameState, Move move) {
			assert gameState == AbstractAi.this.gameState;
			AbstractAi.this.moveDone(move);
		}
	};
	
	private final TurnListener turnListener = new TurnListener() {
		@Override
		public void currentPlayerChanged(GameState gameState, Player oldPlayer,
				Player newPlayer) {
			assert gameState == AbstractAi.this.gameState;
			AbstractAi.this.currentPlayerChanged(oldPlayer, newPlayer);
		}
		@Override
		public void currentRoundChanged(GameState gameState,
				int oldRoundNumber, int newRoundNumber) { }
	};

	protected AbstractAi(GameGraph gameGraph) {
		this.gameGraph = gameGraph;
		// TODO GameState doch ueber Constructor setzen?
	}
	
	protected GameState getGameState() {
		return gameState;
	}
	
	protected GameGraph getGameGraph() {
		return gameGraph;
	}
	
	// Folgendes wird gleich automatisch gemacht bei beginCalculation()
	// und finishCalculation() -- ansonsten wird's wohl nicht gebraucht
//	/** Clears the ready flag */
//	protected void clearReady() {
//		ready = false;
//	}
//
//	/** Sets the ready flag */
//	protected void setReady() {
//		ready = true;
//	}
	
	/**
	 * This method returns the value of the
	 * <code>decideNowFlag</code>, which is set
	 * in the <code>decideNow</code> method.
	 * @return <code>true</code> if someone do not
	 * want to wait longer for the resulting move(s)
	 */
	protected boolean isDecideNowFlagSet() {
		return decideNowFlag;
	}
	
	/**
	 * This method shall be called by the calculation
	 * process when it checked the <code>decideNowFlag</code>.
	 */
	protected void clearDecideNowFlag() {
		decideNowFlag = false;
	}
	
	protected void setTimeLeft(int millis) {
		timeLeft = millis;
	}
	
	/**
	 * Informs the AI listeners accordingly,
	 * clears the <code>ready</code> and
	 * <code>decideNowFlag</code> flags and
	 * sets <code>timeLeft</code> to
	 * <code>UNKNOWN</code>.
	 * 
	 * Call this method when your very concrete
	 * AI starts the calculation.
	 */
	protected void beginCalculation() {
		decideNowFlag = false; // just for the case
		ready = false;
		timeLeft = UNKNOWN_TIME;
		for (AiListener l : listeners) {
			l.beginCalculation(this);
		}
	}
	
	/**
	 * Informs the AI listeners accordingly,
	 * sets the <code>ready</code> flag and
	 * sets <code>timeLeft</code> to
	 * <tt>0</tt>.
	 * 
	 * Call this method when your very concrete
	 * AI finishes the calculation.
	 */
	protected void finishCalculation() {
		ready = true;
		timeLeft = 0;
		for (AiListener l : listeners) {
			l.finishCalculation(this);
		}
	}
	
	// TODO Kommentar bei folgenden vier Methods: startCalc or cancelCalc!!
	/**
	 * This method is called by our private MoveListener
	 * when a move is undone in the GameState.
	 * 
	 * The implementation (in <code>AbstractMrXAi</code>
	 * and <code>AbstractDetectiveAi</code>) should
	 * invoke <code>startCalculation</code> when appropriate.
	 * 
	 * @param move the move which is undone
	 */
	protected abstract void moveUndone(Move move);

	/**
	 * This method is called by our private MoveListener
	 * when a move is done in the GameState.
	 * 
	 * The implementation (in <code>AbstractMrXAi</code>
	 * and <code>AbstractDetectiveAi</code>) should
	 * invoke <code>startCalculation</code> when appropriate.
	 * 
	 * @param move the move which is done
	 */
	protected abstract void moveDone(Move move);

	/**
	 * This method is called by our private TurnListener
	 * when a the current player changes in the GameState.
	 * 
	 * The implementation (in <code>AbstractMrXAi</code>
	 * and <code>AbstractDetectiveAi</code>) should
	 * invoke <code>startCalculation</code> when appropriate.
	 * 
	 * @param oldPlayer
	 * @param newPlayer
	 */
	protected abstract void currentPlayerChanged(Player oldPlayer, Player newPlayer);
	
	/**
	 * This method is called when your AI can start the
	 * calculation. Note that not this but <code>AbstractMrXAi</code>
	 * and <code>AbstractDetectiveAi</code> will call this
	 * method, because they know when to start the calculation!
	 * 
	 * This method shall just initiate the creation of an extra
	 * thread for the calculation. The calculation process has
	 * to call <code>beginCalculation()</code> and
	 * <code>endCalculation()</code>.
	 */
	protected abstract void startCalculation();
	
	
	
	
	// Fertige Implementierungen von public Methoden, die
	// normalerweise nicht mehr überschrieben werden müssen

	@Override
	public void setGameState(GameState gameState) {
		if (this.gameState != null) {
			throw new IllegalStateException("GameState is already set.");
		}
		this.gameState = gameState;
		gameState.addMoveListener(moveListener);
		gameState.addTurnListener(turnListener);
	}
	
	@Override
	public void setGameGraph(GameGraph gameGraph) {
		if (this.gameGraph != null) {
			throw new IllegalStateException("GameGraph is already set.");
		}
		this.gameGraph = gameGraph;
	}

	@Override
	public boolean isReady() {
		return ready;
	}
	
	/**
	 * This method just sets the <code>decideNowFlag</code>.
	 * It can be overridden to do more; such as signal, event or
	 * something like this ...
	 */
	@Override
	public void decideNow() {
		decideNowFlag = true;
	}
	
	@Override
	public int getTimeLeft() {
		return timeLeft;
	}

	@Override
	public int getTimeLimit() {
		return timeLimit;
	}

	@Override
	public void setTimeLimit(int millis) {
		timeLimit = millis;
	}

	@Override
	public void addAiListener(AiListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeAiListener(AiListener listener) {
		listeners.remove(listener);
	}

}
