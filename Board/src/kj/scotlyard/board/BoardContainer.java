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

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.rules.Rules;

@SuppressWarnings("serial")
public class BoardContainer extends JPanel {

	private BoardPanel boardPanel;
	
	private GameGraph gameGraph;
	
	private GameState gameState;
	
	private Rules rules;
	
	private Map<Player, Piece> pieces = new HashMap<>();
		
	
	public GameGraph getGameGraph() {
		return gameGraph;
	}

	public void setGameGraph(GameGraph gameGraph) {
		this.gameGraph = gameGraph;
		
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
		
		// Spielfiguren/Pieces erzeugen
		pieces.put(gameState.getMrX(), new MrXPiece(gameState.getMrX(), ""));
		for (DetectivePlayer d : gameState.getDetectives()) {
			pieces.put(d, new DetectivePiece(d, ""));
		}
	}

	public Rules getRules() {
		return rules;
	}

	public void setRules(Rules rules) {
		this.rules = rules;
	}





	public BoardContainer(Image boardImage, GameGraph gameGraph,
			GameState gameState, Rules rules) {
		
		
	}

	
	
}
