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

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class HistoryPanel extends JPanel {

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
