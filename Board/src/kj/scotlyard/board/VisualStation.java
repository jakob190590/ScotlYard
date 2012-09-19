/*
 * ScotlYard -- A software implementation of the Scotland Yard board game
 * Copyright (C) 2012  Jakob Schöttl, Korbinian Eckstein
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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.Player;

@SuppressWarnings("serial")
public class VisualStation extends JComponent implements PercentalBounds {
	
	// Types of marking
	
	public static enum MarkType {
		/** Mark type for possible positions of MrX */
		POSSIBLE_POSITION,
		
		/** Mark type for the designated next station in an upcoming move */
		DESIGNATED_NEXT_STATION,
		
		/** Mark type for the designated second station in an upcoming double move */
		DESIGNATED_NEXT_STATION2,
		
		/** Mark type for a possible next station in an upcoming move */
		POSSIBLE_NEXT_STATION,
		
		/** Mark type for an impossible next station in an upcoming move */
		IMPOSSIBLE_NEXT_STATION;		
	}
	
	private static final class Marking {
		public final MarkType type;
		public final Player player;
		public Marking(MarkType type, Player player) {
			this.type = type;
			this.player = player;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((player == null) ? 0 : player.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj instanceof Marking) {				
				Marking other = (Marking) obj;
				if (type == other.type && player == other.player)
					return true;
			}
			return false;
		}
	}
	
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(VisualStation.class);
	
	private StationVertex station;
	
	private Set<Marking> markings = new HashSet<>();
	
	private int number;

	private double x2;
	private double y2;
	private double width2;
	private double height2;

	
	public VisualStation() {
		this(null, 0);
	}

	public VisualStation(StationVertex station, int number) {
		super();
		this.station = station;
		this.number = number;
	}
	
	// TODO override paintComponent
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		
		g.setColor(Color.BLACK);
		g2D.drawOval(0, 0, getWidth(), getHeight());
		
		for (Marking m : markings) {
			// TODO draw markings
//			for each geht auch fuer enums, also koennte ich die werte nach prioritaet aufsteigend sortieren, und so zeichnen!
		}
	}
	
	@Override
	public boolean contains(int x, int y) {
		// TODO Korbi, x, y innerhalb oder auf oval mit breite w u. hoehe h
		// (ursprung links oben)
//		return (x >= 0) && (x < getWidth() / 2) && (y >= 0) && (y < getHeight() / 2);
		return super.contains(x, y);
	}

	public StationVertex getStation() {
		return station;
	}

	public void setStation(StationVertex station) {
		this.station = station;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public Point2D.Double getLocation2() {
		return new Point2D.Double(x2, y2);
	}

	@Override
	public void setLocation2(double x, double y) {
		x2 = x;
		y2 = y;
	}

	@Override
	public void setLocation2(Point2D.Double p) {
		x2 = p.x;
		y2 = p.y;
	}

	@Override
	public Dimension getSize2() {
		Dimension dim = new Dimension();
		dim.setSize(width2, height2);
		return dim;
	}

	@Override
	public void setSize2(double width, double height) {
		width2 = width;
		height2 = height;
	}

	@Override
	public void setSize2(Dimension d) {
		width2 = d.getWidth();
		height2 = d.getHeight();
	}

	@Override
	public Rectangle2D.Double getBounds2() {
		return new Rectangle2D.Double(x2, y2, width2, height2);
	}

	@Override
	public void setBounds2(double x, double y, double width, double height) {
		x2 = x;
		y2 = y;
		width2 = width;
		height2 = height;
	}

	@Override
	public void setBounds2(java.awt.geom.Rectangle2D.Double r) {
		x2 = r.x;
		y2 = r.y;
		width2 = r.width;
		height2 = r.height;
	}
	
	// Markings sind NICHT kombinierbar (Zweierpotenzen mit bitweisem ODER |)!
	// Grund: Verbindung mit Player
	// Dadurch wuerde das Interface nur unverstaendlicher.
	
	/**
	 * Enable specified marking.
	 * @param type the mark type
	 * @param player the player for which the marking is
	 */
	public void enableMarking(MarkType type, Player player) {
		markings.add(new Marking(type, player));
		repaint();
	}
	
	/**
	 * Disable specified marking.
	 * @param type the mark type
	 * @param player the player for which the marking is
	 */
	public void disableMarking(MarkType type, Player player) {
		markings.remove(new Marking(type, player));
		repaint();
	}
	
	/**
	 * Disable all markings of the specified type.
	 * @param type the mark type
	 */
	public void disableAllMarkings(MarkType type) { // TODO vllt noch umbenennen: All weg.
		Iterator<Marking> it = markings.iterator();
		while (it.hasNext()) {
			if (it.next().type == type) {
				it.remove();
			}
		}
		repaint();
	}
	
	/**
	 * Disable all markings of the specified player.
	 * @param player the player for which the marking is
	 */
	public void disableAllMarkings(Player player) {
		Iterator<Marking> it = markings.iterator();
		while (it.hasNext()) {
			if (it.next().player == player) {
				it.remove();
			}
		}
		repaint();
	}
	
	/** Disable all markings. */
	public void disableAllMarkings() {
		markings.clear();
		repaint();
	}

	@Override
	public String toString() {
		return getClass() + "[number=" + number + "]";
	}
	
}
