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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class RoundPanel extends JPanel {
	
	private int roundNumber;
	
	private boolean currentRound;
	
	private final Border defaultBorder = BorderFactory.createLineBorder(Color.BLACK);
	private final Border currentRoundBorder = BorderFactory.createBevelBorder(
			BevelBorder.RAISED, Color.RED, Color.RED);
	
	private JPanel movePanelContainer;
	private JLabel lblRoundNumber;
	
	/**
	 * Create the panel.
	 */
	public RoundPanel(int roundNumber) {
		this.roundNumber = roundNumber;

		setCurrentRound(false);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		lblRoundNumber = new JLabel("Round #");
		lblRoundNumber.setAlignmentY(Component.TOP_ALIGNMENT);
		lblRoundNumber.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblRoundNumber.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblRoundNumber.setText(String.valueOf(roundNumber));
		add(lblRoundNumber);
		
		movePanelContainer = new JPanel();
		movePanelContainer.setLayout(new BoxLayout(movePanelContainer, BoxLayout.X_AXIS));
		add(movePanelContainer);

	}
	
	public void addMovePanel(MovePanel movePanel) {
		movePanelContainer.add(movePanel);
	}
	
	// remove all move panels
	public void removeMovePanels() {
		movePanelContainer.removeAll();
	}

	public int getRoundNumber() {
		return roundNumber;
	}

	public boolean isCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(boolean currentRound) {
		this.currentRound = currentRound;
		setBorder(currentRound ? currentRoundBorder : defaultBorder);
	}

}
