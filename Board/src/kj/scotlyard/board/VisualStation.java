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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.Player;

@SuppressWarnings("serial")
public class VisualStation extends JComponent implements PercentalBounds {
	
	private StationVertex station;
	
	private int number;

	private double x2;
	private double y2;
	private double width2;
	private double height2;

	// Types of marking
	
	/** Mark type for possible positions of MrX */
	public static final int POSSIBLE_POSITION = 0;
	
	/** Mark type for the designated next station in an upcoming move */
	public static final int DESIGNATED_NEXT_STATION = 1;
	
	/** Mark type for a possible next station in an upcoming move */
	public static final int POSSIBLE_NEXT_STATION = 2;
	
	/** Mark type for an impossible next station in an upcoming move */
	public static final int IMPOSSIBLE_NEXT_STATION = 3;
	
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
	}
	
	@Override
	public boolean contains(int x, int y) {
		// TODO Korbi, x, y innerhalb oder auf kreis mit durchmesser d
		// kreis liegt im quadrat d^2, ursprung ist links oben; x, y positiv nach rechts unten
		return super.contains(x, y);
	}

	@Override
	protected void processEvent(AWTEvent e) {
//		if (e instanceof MouseEvent) System.out.println("MouseEvent auf VisualStation.");
		if (e instanceof MouseEvent 
				&& !contains(((MouseEvent) e).getX(), ((MouseEvent) e).getY())) {
			// TODO braucht's das ueberhaupt, oder wird das eh automatisch gemacht bei MouseEvents?
			// Mausereignis ausserhalb des Umrisses der Station
			// -> Ereignis nicht bearbeiten (TODO hoffentlich wird's dann dem parent uebergeben?)
//			System.out.println("MouseEvent ausserhalb des Umrisses der VisualStation.");
			return;
		}
		super.processEvent(e);
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

	
	public void enableMarking(int type, Player player) {
		// TODO let's draw marking
		// vllt doch lieber show und hide?
	}
	
	public void disableMarking(int type, Player player) {
		// TODO don't draw marking
	}

	@Override
	public String toString() {
		return getClass().getName() + "[number=" + number + "]";
	}
	
}
