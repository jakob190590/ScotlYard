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

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.undo.UndoManager;

import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MoveListener;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.TurnListener;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.util.GameStateExtension;

@SuppressWarnings("serial")
public class HistoryPanel extends JPanel {
	
	private Rules rules;
	
	private GameState gameState;
	
	private UndoManager undoManager;
	
	private Stack<Move> undoneMoves = new Stack<>();
	
	// Unabhaengig von der "Composition" in der GUI:
	
	private List<RoundPanel> roundPanels = new ArrayList<>();
	
	private List<MovePanel> movePanels = new ArrayList<>();
	
	
	private final MoveListener moveListener = new MoveListener() {
		@Override
		public void movesCleard(GameState gameState) {
//			undoneMoves.clear();
			updateMovePanels();
		}
		@Override
		public void moveUndone(GameState gameState, Move move) {
			if (move.getPlayer() instanceof MrXPlayer) {
//				undoneMoves.push(move);
				updateMovePanels(move.getMoveNumber());
			}
		}
		@Override
		public void moveDone(GameState gameState, Move move) {
			if (move.getPlayer() instanceof MrXPlayer) {
//				if (!undoneMoves.isEmpty() && undoneMoves.pop() != move) {
//					// "Die Zukunft hat sich geaendert"
//					undoneMoves.clear();
//					// Bei allen folgenden MovePanels Daten rausloeschen
//					for (int i = move.getMoveNumber(); i < movePanels.size(); i++) {
//						movePanels.get(i).setMove(null);
//					}
//				}
//
//				// Daten des Moves in MovePanel eintragen (schadet nicht, auch wenn
//				// sich der zug schon steht und durch Redo nur wiederholt wird)
//				if (move.getMoves().isEmpty()) {
//					// Kein Multi Move
//					movePanels.get(move.getMoveNumber()).setMove(move);
//				} else {
//					// Sub Moves
//					for (Move m : move.getMoves()) {
//						movePanels.get(m.getMoveNumber()).setMove(m);
//					}
//				}
				
				// Einfacher:
				List<Move> m = GameStateExtension.flattenMove(move);
				int firstMoveNumber = m.get(0).getMoveNumber();
				updateMovePanels(firstMoveNumber);
				if (!move.getMoves().isEmpty()) {
					// Bei Multi Move
					arrangePanels(firstMoveNumber);
				}
				// Noch einfacher:
//				updateMovePanels();
//				rearrangeMovePanels();
			}

		}
	};

	private final TurnListener turnListener = new TurnListener() {
		@Override
		public void currentRoundChanged(GameState gameState, int oldRoundNumber,
				int newRoundNumber) {
			// Border fuer current round auf aktuelles RoundPanel setzen
			roundPanels.get(oldRoundNumber).setCurrentRound(false);
			
			RoundPanel rp;
			if (newRoundNumber > roundPanels.size()) {
				// Da fehlt ein RoundPanel fuer die neue Runde
				rp = addNewRoundPanel(newRoundNumber);
			} else {
				rp = roundPanels.get(newRoundNumber);
			}
			rp.setCurrentRound(true);
			
			arrangePanels(newRoundNumber);
		}
		@Override
		public void currentPlayerChanged(GameState gameState, Player oldPlayer,
				Player newPlayer) { } // wuesste nicht, was es hier zu tun gaebe
	};
	
	private JPanel roundPanelContainer;

	/**
	 * Create the panel.
	 */
	public HistoryPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel historyPanel = new JPanel();
		add(historyPanel, BorderLayout.CENTER);
		historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.X_AXIS));
		
		JPanel rowHeadPanel = new JPanel();
		historyPanel.add(rowHeadPanel);
		rowHeadPanel.setLayout(new BoxLayout(rowHeadPanel, BoxLayout.Y_AXIS));
		
		JLabel lblRoundNumber = new JLabel("Round Number");
		lblRoundNumber.setHorizontalAlignment(SwingConstants.CENTER);
		lblRoundNumber.setFont(new Font("Tahoma", Font.BOLD, 16));
		rowHeadPanel.add(lblRoundNumber);
		
		JLabel lblMrXMoves = new JLabel("MrX Moves");
		lblMrXMoves.setHorizontalAlignment(SwingConstants.CENTER);
		lblMrXMoves.setFont(new Font("Tahoma", Font.BOLD, 16));
		rowHeadPanel.add(lblMrXMoves);
		
		roundPanelContainer = new JPanel();
		roundPanelContainer.setLayout(new BoxLayout(roundPanelContainer, BoxLayout.X_AXIS));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(roundPanelContainer);
		historyPanel.add(scrollPane);
		
	}
	
	private RoundPanel addNewRoundPanel(int roundNumber) {
		RoundPanel rp = new RoundPanel(roundNumber);
		roundPanels.add(rp);
		return rp;
	}
	
	private MovePanel addNewMovePanel(int moveNumber) {
		MovePanel mp = new MovePanel(moveNumber);
		movePanels.add(mp);
		return mp;
	}
	
	/**
	 * Arrangiert die Round- und MovePanels neu. Dabei wird
	 * dafuer gesorgt, dass alle RoundPanels an der richtigen
	 * Stelle im Viewport des JScrollPane sind und alle
	 * MovePanels im jeweils richtigen RoundPanel.
	 * @param fromMoveNumber ab dieser Move Number wird arrangiert - Parameter wird momentan ignoriert
	 */
	public void arrangePanels(int fromMoveNumber) {
		// Zuerst mal alle MovePanels aus den RoundPanels rausloesen
		for (RoundPanel rp : roundPanels) {
			rp.removeMovePanels();
		}
		
		// Jetzt die MovePanels den RoundPanels wieder richtig hinzufuegen
		int i = 0;
		for (MovePanel mp : movePanels) {
			int roundNumber = mp.getRoundNumber();
			if (roundNumber != MovePanel.NO_ROUND_NUMBER) {
				i = roundNumber + 1;
				roundPanels.get(roundNumber).addMovePanel(mp);
			} else {
				roundPanels.get(i).addMovePanel(mp);
				i++;
			}
		}
		
		// Sind alle MovePanels auch tatsaechlich im Viewport?
		// einfach und ineffizient:
		roundPanelContainer.removeAll();
		for (RoundPanel rp : roundPanels) {
			roundPanelContainer.add(rp);
		}
		
		
//		revalidate(); // falls sich breiten gaendert haben ...
	}

	/**
	 * Arrangiert alle Round- und MovePanels neu. Dabei wird
	 * dafuer gesorgt, dass alle RoundPanels an der richtigen
	 * Stelle im Viewport des JScrollPane sind und alle
	 * MovePanels im jeweils richtigen RoundPanel.
	 */
	public void arrangePanels() {
		// Alle, von Anfang an
		arrangePanels(GameState.INITIAL_MOVE_NUMBER);
	}
	
	/**
	 * Aktualisiert die Daten der MovePanels ab der angegebenen
	 * Move Number gemaess dem GameState. Die "Daten" sind Ticket
	 * und Round Number. Die Methode funktioniert auch bei
	 * <code>gameState == null</code>. Kein GameState (<code>null
	 * </code> macht in Verbindung mit einer Move Number wahrscheinlich
	 * keinen Sinn. In solchen Faellen sollte die parameterlose
	 * Variante verwendet werden.
	 * @param fromMoveNumber ab dieser Move Number wird aktualisiert
	 */
	public void updateMovePanels(int fromMoveNumber) {
		if (gameState == null) {
			// MovePanels auch richtig updaten, wenn GameState
			// nicht gesetzt ist, d.h. naemlich loeschen!
			for (int i = fromMoveNumber; i < movePanels.size(); i++) {
				movePanels.get(i).setMove(null);
			}
		} else {
			// Bei allen MovePanels:
			// Daten auf entsprechenden MrX-Move im GameState setzen
			// oder Daten rausloeschen!
			Move m;
			Iterator<Move> it = GameStateExtension.moveIterator(gameState, gameState.getMrX(), true, fromMoveNumber);
			for (int i = fromMoveNumber; i < movePanels.size(); i++) {
				MovePanel mp = movePanels.get(i);
				m = null;
				while (it.hasNext()) { // while: man weiss ja nie
					m = it.next();
					if (m.getMoveNumber() == i)
						break;
				}
				mp.setMove(m);
			}
			
			// Wenn MrX im GameState noch mehr Moves hat, als es MovePanels gibt:
			// Fuer alle diese Moves noch ein MovePanel erzeugen!
			while (it.hasNext()) {
				m = it.next();
				addNewMovePanel(m.getMoveNumber());
			}
		}
	}
	
	public void updateMovePanels() {
		updateMovePanels(GameState.INITIAL_MOVE_NUMBER);
	}

	public Rules getRules() {
		return rules;
	}

	public void setRules(Rules rules) {
		if (this.rules != rules) {
			this.rules = rules;
			// Alle alten Border entfernen
			for (MovePanel mp : movePanels) {
				mp.setUncoverMove(false);
			}
			
			if (rules != null) {
				// Mindestens so viele MovePanels, bis MrX zum letzten Mal auftaucht
				
				List<Integer> mrXUncoverMoveNumbers = rules.getGameStateAccessPolicy().getMrXUncoverMoveNumbers();
				int lastMoveNumber = 0;
				if (!mrXUncoverMoveNumbers.isEmpty())
					lastMoveNumber = mrXUncoverMoveNumbers.get(mrXUncoverMoveNumbers.size() - 1);
				
				// Fehlende MovePanel hinzufuegen (wenn noetig)
				int i = movePanels.size();
				while (i <= lastMoveNumber) {
					addNewMovePanel(i);
					i++;
				}
				// Fehlende RoundPanel hinzufuegen (wenn noetig)
				i = roundPanels.size();
				while (i <= lastMoveNumber) {
					addNewRoundPanel(i);
					i++;
				}
				
				// Border fuer MovePanels, die MrXUncover Moves sind
				for (int j : mrXUncoverMoveNumbers) {
					movePanels.get(j).setUncoverMove(true);
				}
			}
			arrangePanels();
		}
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		if (this.gameState != gameState) {
			if (this.gameState != null) {
				// Unregister listeners from old GameState
				this.gameState.removeMoveListener(moveListener);
				this.gameState.removeTurnListener(turnListener);
			}
			for (RoundPanel rp : roundPanels) {
				rp.setCurrentRound(false);
			}
			
			this.gameState = gameState;
			undoneMoves.clear();
			if (gameState != null) {
				// Register listeners
				gameState.addMoveListener(moveListener);
				gameState.addTurnListener(turnListener);
				
				// Border fuer Current Round
				int crn = gameState.getCurrentRoundNumber();
				if (!roundPanels.isEmpty()) {
					roundPanels.get(crn).setCurrentRound(true);
				}
			}
			updateMovePanels();
			arrangePanels();
		}
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

}
