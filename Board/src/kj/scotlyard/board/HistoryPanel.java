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
import java.util.List;
import java.util.Stack;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.undo.UndoManager;

import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MoveListener;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.TurnListener;
import kj.scotlyard.game.rules.Rules;

@SuppressWarnings("serial")
public class HistoryPanel extends JPanel {
	
	private Rules rules;
	
	private GameState gameState;
	
	private UndoManager undoManager;
	
	// Unabhaengig von der "Composition" in der GUI:
	
	private List<RoundPanel> roundPanels;
	
	private List<MrXMovePanel> movePanels;
	
	private Stack<Move> undoneMoves = new Stack<>();
	
	private final MoveListener moveListener = new MoveListener() {
		@Override
		public void movesCleard(GameState gameState) {
			undoneMoves.clear();
		}
		@Override
		public void moveUndone(GameState gameState, Move move) {
			undoneMoves.push(move);
		}
		@Override
		public void moveDone(GameState gameState, Move move) {
			if (undoneMoves.isEmpty()) {
				// naechstes MovePanel einrichten
			} else if (undoneMoves.pop() == move) {
				// eigentlich nichts machen, naechstes MovePanel bleibt wie es vorher war
			} else {
				// "Die Zukunft hat sich geaendert"
				undoneMoves.clear();
				// ab einschliesslich naechstem MovePanel alle folgenden Tickets rausloeschen aus den MovePanels
			}
		}
	};

	private final TurnListener turnListener = new TurnListener() {
		@Override
		public void currentRoundChanged(GameState gameState, int oldRoundNumber,
				int newRoundNumber) {
			// Border fuer current round auf aktuelles RoundPanel setzen
		}
		@Override
		public void currentPlayerChanged(GameState gameState, Player oldPlayer,
				Player newPlayer) { } // wuesste nicht, was es hier zu tun gaebe
	};

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
		
		JLabel lblRoundNumber = new JLabel("<html>Round<br/>Number</html>");
		lblRoundNumber.setFont(new Font("Tahoma", Font.BOLD, 16));
		rowHeadPanel.add(lblRoundNumber);
		
		JLabel lblMrx = new JLabel("MrX");
		rowHeadPanel.add(lblMrx);
		
		JLabel lblMoveNumber = new JLabel("<html>Move<br/>Number</html>");
		lblMoveNumber.setFont(new Font("Tahoma", Font.BOLD, 16));
		rowHeadPanel.add(lblMoveNumber);
		
		JPanel historyLinePanel = new JPanel();
		historyLinePanel.setLayout(new BoxLayout(historyLinePanel, BoxLayout.X_AXIS));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(historyLinePanel);
		historyPanel.add(scrollPane);
		
	}

}
