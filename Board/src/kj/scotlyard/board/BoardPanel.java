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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MoveListener;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.PlayerListener;

/**
 * Stellt das Spielbrett mit Stationen und Spielfiguren dar.
 * Dazu wird ein <code>Image</code> angezeigt, das erst 
 * gesetzt werden darf, wenn es vollständig geladen ist.
 * @author jakob190590
 *
 */
@SuppressWarnings("serial")
public class BoardPanel extends JPanel {
	
	private Image image;
	
	private Dimension preferredSize = new Dimension();
	
	private GameState gameState;
	
	private final PlayerListener playerListener = new PlayerListener() {
		
		@Override
		public void mrXSet(GameState gameState, MrXPlayer oldMrX, MrXPlayer newMrX) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void detectiveRemoved(GameState gameState,
				DetectivePlayer detective, int atIndex) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void detectiveAdded(GameState gameState, DetectivePlayer detective,
				int atIndex) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private final MoveListener moveListener = new MoveListener() {
		
		@Override
		public void movesCleard(GameState gameState) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void moveUndone(GameState gameState, Move move) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void moveDone(GameState gameState, Move move) {
			// TODO Auto-generated method stub
			
		}
	};
	
	// Mehr als Move- und PlayerListener brauchen wir fuer's erste nicht:
	// Kleine Indicator Turn oder Item betreffend, sollen Player-Objekte 
	// selbst anzeigen, das ist nicht Aufgabe des BoardPanels.
	
	
	
	public BoardPanel() {
		super(new PercentalLayout());
	}
	
	@Override
	protected void paintComponent(Graphics g) {		
		super.paintComponent(g);
		if (image != null) {
			Graphics2D g2D = (Graphics2D) g;
			g2D.drawImage(image, 0, 0, getWidth(), getHeight(), this);			
		}		
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		if (this.gameState != gameState) {
			if (this.gameState != null) {
				// Unregister listeners from old GameState
				this.gameState.removePlayerListener(playerListener);
				this.gameState.removeMoveListener(moveListener);
			}
			this.gameState = gameState;
			if (gameState != null) {
				// Register listeners at new GameState
				gameState.addPlayerListener(playerListener);
				gameState.addMoveListener(moveListener);
			}
		}
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image boardImage) {
		image = boardImage;
		preferredSize = new Dimension(image.getWidth(null), image.getHeight(null));
//		System.out.println(preferredSize);
	}


	@Override
	public Dimension getPreferredSize() {		
		return preferredSize;	
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		this.preferredSize = preferredSize;
	}
	
}
