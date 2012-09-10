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

import java.awt.Dimension;
import java.awt.geom.Point2D.Double;

import javax.swing.JComponent;

import kj.scotlyard.game.model.Player;

@SuppressWarnings("serial")
public abstract class Piece extends JComponent implements PercentalBounds {

	private Player player;
	
	private String playerName;
	
	public Piece(Player player, String playerName) {
		this.player = player;
		this.playerName = playerName;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public VisualStation getVisualStation() {
		return null; // TODO return visual station
	}
	
	@Override
	public Double getLocation2() {
		return getVisualStation().getLocation2();
	}

	@Override
	public void setLocation2(double x, double y) {
		raiseSetterUnsuppExc();
	}

	@Override
	public void setLocation2(Double p) {
		raiseSetterUnsuppExc();		
	}

	@Override
	public Dimension getSize2() {
		return getVisualStation().getSize2();
	}

	@Override
	public void setSize2(double width, double height) {
		raiseSetterUnsuppExc();
	}

	@Override
	public void setSize2(Dimension d) {
		raiseSetterUnsuppExc();
	}

	@Override
	public java.awt.geom.Rectangle2D.Double getBounds2() {
		return getVisualStation().getBounds2();
	}

	@Override
	public void setBounds2(double x, double y, double width, double height) {
		raiseSetterUnsuppExc();
	}

	@Override
	public void setBounds2(java.awt.geom.Rectangle2D.Double r) {
		raiseSetterUnsuppExc();
	}
	
	private void raiseSetterUnsuppExc() {
		throw new UnsupportedOperationException("Setter not supported; " +
				"value is obtained from the VisualStation.");
	}
	
}
