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

package kj.scotlyard.board.board;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import kj.scotlyard.board.Board;
import kj.scotlyard.board.MovePreparer;
import kj.scotlyard.board.layout.PercentalLayout;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MoveListener;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.PlayerListener;
import kj.scotlyard.game.util.MoveHelper;

/**
 * Stellt das Spielbrett mit Stationen und Spielfiguren dar.
 * Dazu wird ein <code>Image</code> angezeigt, das erst 
 * gesetzt werden darf, wenn es vollständig geladen ist.
 * @author jakob190590
 *
 */
@SuppressWarnings("serial")
public class BoardPanel extends JPanel {
	
	private static final Logger logger = Logger.getLogger(Board.class);
	
	private Image image;
	
	private GameState gameState;
	
	private GameGraph gameGraph;
	
	private Map<Player, Piece> pieces = new HashMap<>();
	
	private Map<StationVertex, VisualStation> visualStations = new HashMap<>();
	
	private MovePreparer movePreparer;
	
	private final MouseListener visualStationMouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			logger.debug("mouse clicked on " + e.getSource());
			super.mouseClicked(e);
			
			// falls auf station geklickt wurde auf der ein spieler steht -> abbruch
			StationVertex s = ((VisualStation) e.getSource()).getStation();
			for (Player p : gameState.getPlayers()) {
				if (s == gameState.getLastMove(p).getStation())
					return;				
			}
			
			Player p = MoveHelper.unambiguousPlayer(gameState, gameGraph, s);
			logger.debug("unambiguous player: " + p);
			if (p == null) {
				// nicht eindeutig -> nimm' momentan ausgewaehlten player
				// TODO vllt doch fragen in diesem fall?
				// JOptionPane.showMessageDialog(BoardPanel.this, "Not quite sure, wich player you want to move. Please click on the player first.", "Confirmation", JOptionPane.QUESTION_MESSAGE);
				// eher nicht, lieber fehlermeldung
				p = movePreparer.getPlayer();
			}
			movePreparer.nextStation(s, p);
		}
	};
	private final MouseListener pieceMouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			
		}
		@Override
		public void mousePressed(MouseEvent e) {
			// Mouse Pressed statt Click wegen Drag & Drop
			super.mousePressed(e);
			Piece p = (Piece) e.getSource();
			movePreparer.selectPlayer(p.getPlayer());
			logger.debug("mouse pressed on " + p.getPlayer());
		}
		
	};
	
	/**
	 * Dieser PlayerListener lässt das BoardPanel reagieren, wenn
	 * Player hinzugefügt oder entfernt werden. Hier können 
	 * <code>NullPointerException</code>s auftreten, wenn die Daten
	 * im BoardPanel (sprich <code>pieces</code>) corrupted sind.
	 */
	private final PlayerListener playerListener = new PlayerListener() {		
		@Override
		public void mrXChanged(GameState gameState, MrXPlayer oldMrX, MrXPlayer newMrX) {
			logger.debug(String.format("Old MrX <%s> replaced by new MrX <%s>", oldMrX, newMrX));
			
			Piece piece;

			// Alten MrX (wenn nicht null) entfernen aus Map und Container
			if (oldMrX != null) {
				piece = pieces.remove(oldMrX);
				piece.removeMouseListener(pieceMouseListener);
				remove(piece);
				repaint();
			}
			
			// Neuen MrX (wenn nicht null) Map und Container hinzufuegen
			if (newMrX != null) {
				piece = new MrXPiece(newMrX);
				piece.setVisible(false);
				piece.addMouseListener(pieceMouseListener);
				pieces.put(newMrX, piece);
				add(piece, 0);
			}
		}
		
		@Override
		public void detectiveRemoved(GameState gameState,
				DetectivePlayer detective, int atIndex) {
			logger.debug("Detective removed: " + detective);
			// Piece aus Map und Container entfernen
			Piece piece = pieces.remove(detective);
			piece.removeMouseListener(pieceMouseListener);
			remove(piece);
			
			repaint();
		}
		
		@Override
		public void detectiveAdded(GameState gameState, DetectivePlayer detective,
				int atIndex) {
			logger.debug("Detective added: " + detective);
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
			piece.addMouseListener(pieceMouseListener);
			pieces.put(detective, piece);			
			add(piece, 0); // TODO sollte revalidate und repaint ausloesen (wenn piece zum ersten mal sichtbar wird)
		}
	};
	
	/**
	 * Dieser MoveListener lässt das BoardPanel auf Move-Ereignisse
	 * reagieren. Hier können <code>NullPointerException</code>s auftreten,
	 * wenn die Daten im BoardPanel (sprich <code>pieces</code>) corrupted
	 * sind.
	 */
	private final MoveListener moveListener = new MoveListener() {
		
		@Override
		public void movesCleard(GameState gameState) {
			logger.debug("Moves cleared, making player invisible");
			for (Piece p : pieces.values()) {
				p.setVisible(false);
			}
		}
		
		@Override
		public void moveUndone(GameState gameState, Move move) {
			logger.debug("Moves undone");
			// VisualStation des Pieces entsprechend setzen
			// Falls der GameState hier ungueltige Params liefert,
			// oder das BoardPanel corrupted ist, gibt's Exception!
			Player p = move.getPlayer();
			Move m = gameState.getLastMove(p);
			if (m == null) {
				pieces.get(p).setVisible(false);
			} else {
				pieces.get(p).setVisualStation(visualStations.get(m.getStation()));				
				revalidate();
				
			}
		}
		
		@Override
		public void moveDone(GameState gameState, Move move) {
			logger.debug("Moves done");
			// VisualStation des Pieces entsprechend setzen
			// Falls der GameState hier ungueltige Params liefert,
			// oder das BoardPanel corrupted ist, gibt's Exception!
			Piece p = pieces.get(move.getPlayer());
			p.setVisualStation(visualStations.get(move.getStation()));
			p.setVisible(true); // falls es initial move ist
			
			revalidate(); // warum wird eigentlich repainted?
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
			Insets insets = getInsets();
			g2D.drawImage(image, insets.left, insets.top, 
					getWidth() - insets.left - insets.right, 
					getHeight() - insets.top - insets.bottom, this);			
		}		
	}

	/**
	 * Builds the <code>visualStations</code> <code>Map</code>, which
	 * maps a <code>StationVertex</code> to it's <code>VisualStation</code>.
	 * This method must be called after <code>VisualStation</code>s
	 * are added to or removed from this Container. 
	 */
	// Must be called, after all VisualComponents of a Graph are added to the component
	public void buildVisualStationMap() {
		for (Component c : getComponents()) {
			if (c instanceof VisualStation) {
				VisualStation vs = (VisualStation) c;
				vs.removeMouseListener(visualStationMouseListener);
			}
		}
		visualStations.clear();
		
		for (Component c : getComponents()) {
			if (c instanceof VisualStation) {
				VisualStation vs = (VisualStation) c;
				vs.addMouseListener(visualStationMouseListener);
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
			}
			
			// Alle Pieces loeschen
			for (Piece p : pieces.values()) {
				p.removeMouseListener(pieceMouseListener);
				remove(p);
			}
			pieces.clear();
			logger.debug("setGameState: All pieces removed");
			
			this.gameState = gameState;
			if (gameState != null) {
				// Register listeners at new GameState
				gameState.addPlayerListener(playerListener);
				gameState.addMoveListener(moveListener);

				// Add new pieces and set visibility/VisualStation
				MrXPlayer mrX = gameState.getMrX(); 
				if (mrX != null) {
					pieces.put(mrX, new MrXPiece(mrX));
				}
				for (DetectivePlayer p : gameState.getDetectives()) {
					pieces.put(p, new DetectivePiece(p));
				}
				for (Map.Entry<Player, Piece> e : pieces.entrySet()) {
					Piece p = e.getValue();
					p.addMouseListener(pieceMouseListener);
					add(p, 0);
					Move m = gameState.getLastMove(e.getKey());
					if (m == null) {
						p.setVisible(false);
					} else {
						p.setVisualStation(visualStations.get(m.getStation()));
					}
				}
				
				revalidate();
			}
			
			repaint();
			
			logger.debug("BoardPanel repainted");
		}
	}

	public GameGraph getGameGraph() {
		return gameGraph;
	}

	public MovePreparer getMovePreparer() {
		return movePreparer;
	}


	public void setMovePreparer(MovePreparer movePreparer) {
		this.movePreparer = movePreparer;
	}


	public void setGameGraph(GameGraph gameGraph) {
		this.gameGraph = gameGraph;
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
		this.image = image;
		repaint();
	}
	
//	Vllt brauch ich's hier auch nicht...
//	private final Observer movePreparerObserver = new Observer() {
//		@Override
//		public void update(Observable o, Object arg) {
//			if (o == mPrep) {
//				if (arg instanceof Player) {
//					setSelectedPlayer((Player) arg);
//				}
//			}
//		}
//	};

}
