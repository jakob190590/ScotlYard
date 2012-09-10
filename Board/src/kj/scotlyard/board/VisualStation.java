package kj.scotlyard.board;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import kj.scotlyard.game.graph.StationVertex;

@SuppressWarnings("serial")
public class VisualStation extends JComponent implements PercentalBounds {
	
	private StationVertex station;
	
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
	
	// TODO override the "event dispatcher method", that informs mouse listeners -> circle

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

}
