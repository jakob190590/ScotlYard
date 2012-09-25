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

public abstract class AbstractAi implements Ai {
	
	private GameState gameState;
	
	private GameGraph gameGraph;
	
	private boolean ready;

	private int timeLimit;

	private Set<AiListener> listeners = new HashSet<>();
	
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
	
	/** Clears the ready flag */
	protected void clearReady() {
		ready = false;
	}
	
	/** Sets the ready flag */
	protected void setReady() {
		ready = true;
	}
	
	/**
	 * Informs the AI listeners accordingly.
	 * Call this method when your AI starts
	 * the calculation.
	 */
	protected void beginCalculation() {
		for (AiListener l : listeners) {
			l.beginCalculation(this);
		}
	}
	
	/**
	 * Informs the AI listeners accordingly.
	 * Call this method when your AI finishes
	 * the calculation.
	 */
	protected void finishCalculation() {
		for (AiListener l : listeners) {
			l.finishCalculation(this);
		}
	}

	@Override
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	@Override
	public boolean isReady() {
		return ready;
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
