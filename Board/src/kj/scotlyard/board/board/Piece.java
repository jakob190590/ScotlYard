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

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import kj.scotlyard.board.layout.PercentalBounds;
import kj.scotlyard.game.model.Player;

@SuppressWarnings("serial")
public abstract class Piece extends JComponent implements PercentalBounds {

	private Player player;
	
	private VisualStation visualStation;
	
	public Piece(Player player) {
		setPlayer(player);
	}
	
	private void updateToolTipText() {
		String s = "";
		if (player != null) {
			s = player.toString();  // TODO gscheider tooltip bzw. player name
			if (visualStation != null)
				s += String.format(" at %s", visualStation.getToolTipText());
		}
		setToolTipText(s);
		
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
		updateToolTipText();
	}

	public VisualStation getVisualStation() {
		return visualStation;
	}

	public void setVisualStation(VisualStation visualStation) {
		this.visualStation = visualStation;
		updateToolTipText();
	}

	@Override
	public Point2D.Double getLocation2() {
		if (getVisualStation() == null)
			return new Point2D.Double();
		return getVisualStation().getLocation2();
	}

	@Override
	public void setLocation2(double x, double y) {
		raiseSetterUnsuppExc();
	}

	@Override
	public void setLocation2(Point2D.Double p) {
		raiseSetterUnsuppExc();		
	}

	@Override
	public Dimension getSize2() {
		if (getVisualStation() == null)
			return new Dimension();
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
	public Rectangle2D.Double getBounds2() {
		if (getVisualStation() == null)
			return new Rectangle2D.Double();
		return getVisualStation().getBounds2();
	}

	@Override
	public void setBounds2(double x, double y, double width, double height) {
		raiseSetterUnsuppExc();
	}

	@Override
	public void setBounds2(Rectangle2D.Double r) {
		raiseSetterUnsuppExc();
	}
	
	private void raiseSetterUnsuppExc() {
		throw new UnsupportedOperationException("Setter not supported; " +
				"value is obtained from the VisualStation.");
	}
	
}
