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

package kj.scotlyard.board.layout;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * An interface that provides location and size hints for a
 * <code>PercentalLayout</code> layout manager.
 * @author jakob190590
 *
 */
public interface PercentalBounds {
	
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
