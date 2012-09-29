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

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import kj.scotlyard.board.metadata.GameMetaData;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Player;

@SuppressWarnings("serial")
public class PlayerComboBoxRenderer extends JLabel implements
		ListCellRenderer<Player> {

	private GameState gameState;
	
	public PlayerComboBoxRenderer() {
		setOpaque(true);
		setVerticalAlignment(CENTER);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Player> list,
			Player value, int index, boolean isSelected, boolean cellHasFocus) {

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		if (value != null) {
			// TODO evtl farbe je nach player unterschiedlich
			setFont(list.getFont());
			setText(GameMetaData.getForPlayer(value).getName()
					+ ((value instanceof DetectivePlayer) ?
								(" - " + Integer.toHexString(value.hashCode())) : ""));
			
			if (gameState != null && gameState.getCurrentPlayer() == value) {
				setBackground(Color.RED); // TODO unschoen, und bis jetzt unabhaengig von isSelected!
			}
		}

		return this;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

}
