package kj.scotlyard.board;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * An interface that provides location and size hints for a
 * <code>PercentalLayout</code> layout manager.
 * @author jakob190590
 *
 */
interface PercentalBounds {
	
	// TODO rename all methods with suffix '2' or 'Percental'

	Point2D.Double getLocation2();

	void setLocation2(double x, double y);

	void setLocation2(Point2D.Double p);

	Dimension getSize2();

	void setSize2(double width, double height);

	void setSize2(Dimension d);

	Rectangle2D.Double getBounds2();

	void setBounds2(double x, double y, double width, double height);

	void setBounds2(Rectangle2D.Double r);

}
