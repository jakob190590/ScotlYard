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
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
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
			undoneMoves.clear();
		}
		@Override
		public void moveUndone(GameState gameState, Move move) {
			if (move.getPlayer() instanceof MrXPlayer) {
				undoneMoves.push(move);
			}
		}
		@Override
		public void moveDone(GameState gameState, Move move) {
			if (move.getPlayer() instanceof MrXPlayer) {
				if (!undoneMoves.isEmpty() && undoneMoves.pop() != move) {
					// "Die Zukunft hat sich geaendert"
					undoneMoves.clear();
					// Bei allen folgenden MovePanels Daten rausloeschen
					for (int i = move.getMoveNumber(); i < movePanels.size(); i++) {
						movePanels.get(i).setMove(null);
					}
				}
					
				// Daten des Moves in MovePanel eintragen (schadet nicht, auch wenn
				// sich der zug schon steht und durch Redo nur wiederholt wird)
				if (move.getMoves().isEmpty()) {
					// Kein Multi Move
					movePanels.get(move.getMoveNumber()).setMove(move);
				} else {
					// Sub Moves
					for (Move m : move.getMoves()) {
						movePanels.get(m.getMoveNumber()).setMove(m);
					}
				}
			}

		}
	};

	private final TurnListener turnListener = new TurnListener() {
		@Override
		public void currentRoundChanged(GameState gameState, int oldRoundNumber,
				int newRoundNumber) {
			// Border fuer current round auf aktuelles RoundPanel setzen
			roundPanels.get(oldRoundNumber).setBorder(null);
			RoundPanel rp;
			if (newRoundNumber > roundPanels.size()) {
				// Da fehlt ein RoundPanel fuer die neue Runde
				rp = new RoundPanel(newRoundNumber);
				roundPanels.add(rp);
			} else {
				rp = roundPanels.get(newRoundNumber);
			}
			rp.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.RED, Color.RED));
			
			rearrangeMovePanels(newRoundNumber);
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
		
		JLabel lblMrx = new JLabel("MrX");
		lblMrx.setHorizontalAlignment(SwingConstants.CENTER);
		rowHeadPanel.add(lblMrx);
		
		JLabel lblMoveNumber = new JLabel("Move Number");
		lblMoveNumber.setHorizontalAlignment(SwingConstants.CENTER);
		lblMoveNumber.setFont(new Font("Tahoma", Font.BOLD, 16));
		rowHeadPanel.add(lblMoveNumber);
		
		roundPanelContainer = new JPanel();
		roundPanelContainer.setLayout(new BoxLayout(roundPanelContainer, BoxLayout.X_AXIS));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(roundPanelContainer);
		historyPanel.add(scrollPane);
		
	}
	
	public void rearrangeMovePanels(int fromMoveNumber) {
		// Zuerst mal alle MovePanels aus den RoundPanels rausloesen
//		int fromRoundNumber = movePanels.get(fromMoveNumber).getRoundNumber();
//		for (int i = fromRoundNumber; i < roundPanels.size(); i++) {
//			roundPanels.get(i).removeMovePanels();
//		}
		for (RoundPanel rp : roundPanels) {
			rp.removeMovePanels();
		}
		
		// Jetzt die MovePanels den RoundPanels wieder richtig hinzufuegen
//		for (int i = fromMoveNumber; i < movePanels.size(); i++) {
//			MovePanel mp = movePanels.get(i);
//			int roundNumber = mp.getRoundNumber();
//			RoundPanel rp;
//			if (roundPanels.size() <= mp.getRoundNumber()) {
//				// passt, RoundPanel vorhanden
//				rp = roundPanels.get(roundNumber);
//			} else {
//				// noch kein RoundPanel, in das wir das MovePanel betten koennen -> anlegen
//				rp = new RoundPanel(roundNumber);
//				roundPanels.add(rp);
//			}
//			rp.addMovePanel(mp);
//		}
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

	public void rearrangeMovePanels() {
		// Alle, von Anfang an
		rearrangeMovePanels(0);
	}

	public Rules getRules() {
		return rules;
	}

	public void setRules(Rules rules) {
		if (this.rules != rules) {
			if (this.rules != null) {
			}
			this.rules = rules;
			// Alle alten Border entfernen
			for (MovePanel mp : movePanels) {
				mp.setBorder(null);
			}
			if (rules != null) {
				// Mindestens so viele MovePanels, bis MrX zum letzten Mal auftaucht
				
				List<Integer> mrXUncoverMoveNumbers = rules.getGameStateAccessPolicy().getMrXUncoverMoveNumbers();
				int lastMoveNumber = mrXUncoverMoveNumbers.get(mrXUncoverMoveNumbers.size() - 1);
				// Fehlende MovePanel hinzufuegen (wenn noetig)
				int i = movePanels.size();
				while (i <= lastMoveNumber) {
					movePanels.add(new MovePanel(i));
					i++;
				}
				// Fehlende RoundPanel hinzufuegen (wenn noetig)
				i = roundPanels.size();
				while (i <= lastMoveNumber) {
					roundPanels.add(new RoundPanel(i));
					i++;
				}
				
				// Border fuer MovePanels, die MrXUncover Moves sind
				for (int j : mrXUncoverMoveNumbers) {
					movePanels.get(j).setBorder(BorderFactory.createBevelBorder(
							BevelBorder.RAISED, Color.BLUE, Color.BLUE));
				}
			}
			rearrangeMovePanels();
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
			this.gameState = gameState;
			undoneMoves.clear();
			if (gameState != null) {
				// Register listeners
				gameState.addMoveListener(moveListener);
				gameState.addTurnListener(turnListener);
				
				// Fehlende MovePanel hinzufuegen (auffuellen)
				if (!gameState.getMoves().isEmpty()) {
					Move lm = GameStateExtension.getLastMoveFlat(gameState, gameState.getMrX());
					if (lm != null) {
						int lmn = lm.getMoveNumber(); // last move number
						int i = movePanels.size();
						while (i < lmn) {
							movePanels.add(new MovePanel(i));
							i++;
						}
					}
				}
			}
			rearrangeMovePanels();
		}
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

}
