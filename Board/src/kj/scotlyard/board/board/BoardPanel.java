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
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import kj.scotlyard.board.MovePreparationEvent;
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
import kj.scotlyard.game.model.TurnListener;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.util.GameStateExtension;
import kj.scotlyard.game.util.MoveHelper;

import org.apache.log4j.Logger;

/**
 * Stellt das Spielbrett mit Stationen und Spielfiguren dar.
 * Dazu wird ein <code>Image</code> angezeigt, das erst
 * gesetzt werden darf, wenn es vollständig geladen ist.
 * @author jakob190590
 *
 */
@SuppressWarnings("serial")
public class BoardPanel extends JPanel {

	private static final Logger logger = Logger.getLogger(BoardPanel.class);

	private Image image;

	private Rules rules;

	private GameState gameState;

	private GameGraph gameGraph;

	private Map<Player, Piece> pieces = new HashMap<>();

	private Map<StationVertex, VisualStation> visualStations = new HashMap<>();

	private MovePreparer movePreparer;

	private boolean mrXAlwaysVisible;

	private final MouseListener visualStationMouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			logger.debug("mouse click on " + e.getSource());
			StationVertex s = ((VisualStation) e.getSource()).getStation();

			// Wenn Detectives dran sind, versuchen,
			// automatisch den Player zu bestimmen
			if (movePreparer.getSelectedPlayer() instanceof DetectivePlayer) {
				
				Set<DetectivePlayer> detectives = MoveHelper.getDetectivesInVicinity(gameState, gameGraph, s, 1);
				if (detectives.contains(movePreparer.getSelectedPlayer())) {
					// Selected Player ist in der Umgebung
					
					if (detectives.size() > 1) {
						// msg: fuer diese station kommen mehrere detective in frage. momentan ausgewaehlten det. verwenden? ja/cancel
					}
				} else if (detectives.size() == 1) {
					// Eindeutig -- das heisst aber noch nicht dass auch select geht!
					Player p = detectives.iterator().next();
					logger.debug("unambiguous detective: " + p);
					movePreparer.selectPlayer(p); // ... einfach ausprobieren, geht ja nicht anders
				} else if (detectives.size() > 1) {
					JOptionPane.showMessageDialog(BoardPanel.this, "Not quite sure, wich " +
							"detective you want to move. Please select the player first or " +
							"use drag and drop or station number input.",
							"Automatic player selection", JOptionPane.WARNING_MESSAGE);
				}

			}
			movePreparer.nextStation(s);
		}
	};
	private final MouseListener pieceMouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			Piece piece = (Piece) e.getSource();
			Player player = piece.getPlayer();
			if (player instanceof MrXPlayer
					&& movePreparer.getSelectedPlayer() instanceof DetectivePlayer) {
				// Detective klickt auf MrX -> Klick an VisualStation durchreichen
				VisualStation vs = piece.getVisualStation();
				MouseEvent e1 = SwingUtilities.convertMouseEvent(piece, e, vs);
				logger.debug(String.format("klick weiterleiten an VisualStation auf %d, %d", e1.getX(), e1.getY()));
				vs.dispatchEvent(e1);
				e.consume(); // gilt in keiner Weise fuer das Piece
			} else {
				// Ansonsten Player auswählen
				movePreparer.selectPlayer(player);
			}
		}
		@Override
		public void mousePressed(MouseEvent e) {
			// Mouse Pressed statt Click wegen Drag & Drop
			// nein, es gibt doch DragSource und startDrag ...
			super.mousePressed(e);
		}

	};

	private final Observer movePreparerObserver = new Observer() {
		@Override
		public void update(Observable o, Object arg) {
			MovePreparationEvent mpe;
			if (arg instanceof MovePreparationEvent && (mpe = (MovePreparationEvent) arg)
					.getId() == MovePreparationEvent.SELECT_PLAYER) {
				for (Piece p : pieces.values()) {
					p.setSelected(mpe.getPlayer() == p.getPlayer());
				}
			}
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
				// grade erst hinzugefuegt -> kann noch keinen
				// initial move haben (mit GameController)
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
			/*
			 * This happens when a detective is shifted up or
			 * down in the list: It will be removed and then
			 * inserted at an other index in the list.
			 * I think that's the only case; provided that you
			 * use the GameController!
			 */
			updatePieceVisibility(piece);
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
				updatePieceVisibility(pieces.get(p));
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
			updatePieceVisibility(p);

			revalidate(); // warum wird eigentlich repainted?
		}
	};
	
	private final TurnListener turnListener = new TurnListener() {
		@Override
		public void currentRoundChanged(GameState gameState, int oldRoundNumber,
				int newRoundNumber) { }
		@Override
		public void currentPlayerChanged(GameState gameState, Player oldPlayer,
				Player newPlayer) {
			for (Piece p : pieces.values()) {
				logger.debug("setCurrent: " + (newPlayer == p.getPlayer()));
				p.setCurrent(newPlayer == p.getPlayer());
			}
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

	public Rules getRules() {
		return rules;
	}

	public void setRules(Rules rules) {
		this.rules = rules;
		MrXPlayer mrX = gameState.getMrX();
		if (mrX != null)
			updatePieceVisibility(pieces.get(mrX));
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
				this.gameState.removeTurnListener(turnListener);
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
				gameState.addTurnListener(turnListener);

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
					add(p, 0); // oben einfuegen
					updatePieceVisibility(p);
					Move m = gameState.getLastMove(e.getKey());
					if (m != null) {
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

	public void setGameGraph(GameGraph gameGraph) {
		this.gameGraph = gameGraph;
	}

	public MovePreparer getMovePreparer() {
		return movePreparer;
	}


	public void setMovePreparer(MovePreparer movePreparer) {
		if (movePreparer != this.movePreparer) {
			if (this.gameState != null) {
				// unregister listeners/observers
				movePreparer.deleteObserver(movePreparerObserver);
			}
			this.movePreparer = movePreparer;
			if (gameState != null) {
				// register listeners/observers
				movePreparer.addObserver(movePreparerObserver);
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
		this.image = image;
		repaint();
	}

	public boolean isMrXAlwaysVisible() {
		return mrXAlwaysVisible;
	}

	public void setMrXAlwaysVisible(boolean mrXAlwaysVisible) {
		this.mrXAlwaysVisible = mrXAlwaysVisible;
		Player mrX;
		Piece p;
		if (gameState != null && (mrX = gameState.getMrX()) != null
				&& (p = pieces.get(mrX)) != null)
			updatePieceVisibility(p);
	}

	private void updatePieceVisibility(Piece piece) {
		Player player = piece.getPlayer();
		Move lmf = GameStateExtension.getLastMoveFlat(gameState, player);
		if (player instanceof MrXPlayer) {
			// Piece sichtbar machen, wenn es mindestens einen Move hat UND
			// entweder immer sichtbar ist ODER eben in dieser Runde (wenn regeln verfuegbar)
			piece.setVisible(lmf != null &&
					(mrXAlwaysVisible ||
					rules == null ||
					rules.getGameStateAccessPolicy().getMrXUncoverMoveNumbers()
							.contains(lmf.getMoveNumber())));
		} else {
			piece.setVisible(lmf != null);
		}
	}

}
