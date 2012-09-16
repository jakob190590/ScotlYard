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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MoveListener;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
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
	
	private GameState gameState;
	
	private Map<Player, Piece> pieces = new HashMap<>();
	
	private Map<StationVertex, VisualStation> visualStations = new HashMap<>();
	
	/**
	 * Dieser PlayerListener laesst das BoardPanel reagieren, wenn
	 * Player hinzugefuegt oder entfernt werden. Hier koennen 
	 * <code>NullPointerException</code>s auftreten, wenn die Daten
	 * im BoardPanel corrupted sind.
	 */
	private final PlayerListener playerListener = new PlayerListener() {
		// TODO testen: wird der layoutmgr automatisch aufgerufen ?
		@Override
		public void mrXSet(GameState gameState, MrXPlayer oldMrX, MrXPlayer newMrX) {
			Piece piece;

			// Alten MrX (wenn nicht null) entfernen aus Map und Container
			if (oldMrX != null) {
				piece = pieces.remove(oldMrX);
				remove(piece);
			}
			
			// Neuen MrX (wenn nicht null) Map und Container hinzufuegen
			if (newMrX != null) {
				piece = new MrXPiece(newMrX);
				piece.setVisible(false);
				pieces.put(newMrX, piece);
				add(piece);
			}
		}
		
		@Override
		public void detectiveRemoved(GameState gameState,
				DetectivePlayer detective, int atIndex) {
			// Piece aus Map und Container entfernen
			Piece piece = pieces.remove(detective);
			remove(piece);
		}
		
		@Override
		public void detectiveAdded(GameState gameState, DetectivePlayer detective,
				int atIndex) {
			// Piece Map und Container hinzufuegen
			Piece piece = new DetectivePiece(detective);
			if (gameState.getLastMove(detective) == null) {
				/*
				 * This happens when a detective is shifted up or
				 * down in the list: It will be removed and then 
				 * inserted at an other index in the list.
				 * I think that's the only case; provided that you
				 * use the GameController!
				 */
				piece.setVisible(false);
			}
			pieces.put(detective, piece);
			add(piece);
		}
	};
	
	/**
	 * Dieser MoveListener laesst das BoardPanel auf Move-Ereignisse
	 * reagieren. Hier koennen <code>NullPointerException</code>s auftreten,
	 * wenn die Daten im BoardPanel corrupted sind.
	 */
	private final MoveListener moveListener = new MoveListener() {
		
		@Override
		public void movesCleard(GameState gameState) {
			for (Piece p : pieces.values()) {
				p.setVisible(false);
			}
			// TODO evtl. markings von stationen disablen
		}
		
		@Override
		public void moveUndone(GameState gameState, Move move) {
			// VisualStation des Pieces entsprechend setzen
			// Falls der GameState hier ungueltige Params liefert,
			// oder das BoardPanel corrupted ist, gibt's Exception!
			Player p = move.getPlayer();
			Move m = gameState.getLastMove(p);
			if (m == null) {
				pieces.get(p).setVisible(false);
			} else {
				pieces.get(p).setVisualStation(visualStations.get(m.getStation()));				
			}
			// TODO evtl. markings von stationen disablen
		}
		
		@Override
		public void moveDone(GameState gameState, Move move) {
			// VisualStation des Pieces entsprechend setzen
			// Falls der GameState hier ungueltige Params liefert,
			// oder das BoardPanel corrupted ist, gibt's Exception!
			Piece p = pieces.get(move.getPlayer());
			p.setVisualStation(visualStations.get(move.getStation()));
			p.setVisible(true); // falls es initial move ist
			
			// TODO evtl. markings von stationen disablen
		}
	};
	
	// Mehr als Move- und PlayerListener brauchen wir fuer's erste nicht:
	// Kleine Indicator Turn oder Item betreffend, sollen Pieces 
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

	/**
	 * Builds the <code>visualStations</code> <code>Map</code>, which
	 * maps a <code>StationVertex</code> to it's <code>VisualStation</code>.
	 * This method must be called after <code>VisualStation</code>s
	 * are added to or removed from this Container. 
	 */
	public void buildVisualStationMap() {
		visualStations.clear();
		for (Component c : getComponents()) {
			if (c instanceof VisualStation) {
				VisualStation vs = (VisualStation) c;
				visualStations.put(vs.getStation(), vs);
			}
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
				// Alle Pieces loeschen
				for (Piece p : pieces.values()) {
					remove(p);
				}
				pieces.clear();
			}
			this.gameState = gameState;
			if (gameState != null) {
				// Register listeners at new GameState
				gameState.addPlayerListener(playerListener);
				gameState.addMoveListener(moveListener);
				
				// Alte Pieces loeschen 
				for (Piece p : pieces.values()) {
					remove(p);
				}
				pieces.clear();
				
				// Neue Pieces adden und Visibility/Station setzen
				MrXPlayer mrX = gameState.getMrX(); 
				if (mrX != null) {
					pieces.put(mrX, new MrXPiece(mrX));
				}
				for (DetectivePlayer p : gameState.getDetectives()) {
					pieces.put(p, new DetectivePiece(p));
				}
				for (Map.Entry<Player, Piece> e : pieces.entrySet()) {
					Piece p = e.getValue();
					add(p);
					Move m = gameState.getLastMove(e.getKey());
					if (m == null) {
						p.setVisible(false);
					} else {
						p.setVisualStation(visualStations.get(m.getStation()));
					}
				}
			}
		}
	}

	public Image getImage() {
		return image;
	}

	/**
	 * Sets the image for this BoardPanel. The image must be loaded
	 * completely before it can be passed as an argument. This is
	 * because we instantly need the dimension of the image.
	 * The param can be <code>null</code>, if no image should be used.
	 * @param image a completely loaded <code>Image</code> object
	 */
	public void setImage(Image image) {
		int w = 0;
		int h = 0;
		if (image != null) {
			w = image.getWidth(null);
			h = image.getHeight(null);
			if (w < 0 || h < 0) {
				throw new IllegalArgumentException("The image seems to be not loaded " +
						"completely: Cannot determine image's width and/or height.");
			}
		}
		this.image = image;
		setPreferredSize(new Dimension(w, h));
//		System.out.println(preferredSize);
	}

}
