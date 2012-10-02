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

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class RoundPanel extends JPanel {
	
	private int roundNumber;
	
	private JPanel movePanelContainer;
	private JLabel lblRoundNumber;
	
	/**
	 * Create the panel.
	 */
	public RoundPanel(int roundNumber) {
		this.roundNumber = roundNumber;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		lblRoundNumber = new JLabel("Round #");
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

}
