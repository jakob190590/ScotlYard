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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.rules.Rules;

@SuppressWarnings("serial")
public class BoardPanel extends JPanel {

	private Image boardImage;
	
	private GameGraph gameGraph;
	
	private GameState gameState;
	
	private Rules rules;
	
	private Rectangle boardImageRect = new Rectangle();
	
	private Map<Player, Piece> pieces = new HashMap<>();
	
	
	private void calculateBoardImageRectangle() {
		
		boardImageRect.x = 0; // left
		boardImageRect.y = 0; // top
		boardImageRect.width = getWidth();
		boardImageRect.height = getHeight();
		
		if (boardImage != null) {
				
			boardImageRect.width = boardImage.getWidth(null);
			boardImageRect.height = boardImage.getHeight(null);
			double ar = (double) boardImageRect.width / boardImageRect.height; // aspect ratio
			
			int wp = getWidth();
			int hp = getHeight();
			
			double hpxar = hp * ar;
			if (hpxar > wp) { 
				// -> auf breite anpassen
				boardImageRect.width = wp;
				boardImageRect.height = (int) (wp / ar);
				boardImageRect.y = (hp - boardImageRect.height) / 2;
			} else {
				// -> auf hoehe anpassen
				boardImageRect.width = (int) hpxar;
				boardImageRect.height = hp;
				boardImageRect.x = (wp - boardImageRect.width) / 2;
			}
		}
	}
	
	private void calculatePieceLocations() {
		for (Piece pp : pieces.values()) { // for each playing piece
			Move m = gameState.getLastMove(pp.getPlayer());
			if (m != null) {
				Point2D.Double p = map.get(m.getStation()); // TODO map fuer stations einfuegen. soll enthalten: coords, isIniStation, StationComponent
				p.setLocation((int) (boardImageRect.getWidth() * p.x - boardImageRect.getX()),
						(int) (boardImageRect.getHeight() * p.y - boardImageRect.getY()));				
			}
		}
	}
	
	
	
	
	
	private Image getBoardImage() {
		return boardImage;
	}

	private void setBoardImage(Image boardImage) {
		this.boardImage = boardImage;
	}

	private GameGraph getGameGraph() {
		return gameGraph;
	}

	private void setGameGraph(GameGraph gameGraph) {
		this.gameGraph = gameGraph;
		
	}

	private GameState getGameState() {
		return gameState;
	}

	private void setGameState(GameState gameState) {
		this.gameState = gameState;
		
		// Spielfiguren/Pieces erzeugen
		pieces.put(gameState.getMrX(), new MrXPiece(gameState.getMrX(), ""));
		for (DetectivePlayer d : gameState.getDetectives()) {
			pieces.put(d, new DetectivePiece(d, ""));
		}
	}

	private Rules getRules() {
		return rules;
	}

	private void setRules(Rules rules) {
		this.rules = rules;
	}





	public BoardPanel(Image boardImage, GameGraph gameGraph,
			GameState gameState, Rules rules) {
		
		this.boardImage = boardImage;
		this.gameGraph = gameGraph;
		this.gameState = gameState;
		this.rules = rules;
		
		calculateBoardImageRectangle();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				calculateBoardImageRectangle();
				calculatePieceLocations();
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2D = (Graphics2D) g;
		
		if (boardImage != null) {
			g2D.drawImage(boardImage, boardImageRect.x, boardImageRect.y,
					boardImageRect.width, boardImageRect.height, this);
			
		}
		
	}
	
}
