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

	/**
	 * Create the panel.
	 */
	public RoundPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel lblRoundNumber = new JLabel("Round Number");
		lblRoundNumber.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(lblRoundNumber);
		
		JPanel mrXMovesPanel = new JPanel();
		add(mrXMovesPanel);
		mrXMovesPanel.setLayout(new BoxLayout(mrXMovesPanel, BoxLayout.X_AXIS));

	}

}
