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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.geom.Rectangle2D;

/**
 * A layout manager that behaves like the <code>null</code> layout
 * for normal components, but lays out <code>PercentalBounds</code>
 * objects according to it's percental location and size hints.
 * @author jakob190590
 *
 */
public class PercentalLayout implements LayoutManager {

	@Override
	public void addLayoutComponent(String name, Component comp) { }

	@Override
	public void removeLayoutComponent(Component comp) { }

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return parent.getPreferredSize();
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		Insets insets = parent.getInsets();
		return new Dimension(insets.left + insets.right, 
				insets.top + insets.bottom);
	}

	@Override
	public void layoutContainer(Container parent) {
		Insets insets = parent.getInsets();
		for (Component c : parent.getComponents()) {
			if (c instanceof PercentalBounds) {
				PercentalBounds pb = (PercentalBounds) c;				
				Rectangle2D.Double bounds = pb.getBounds2();
				int w = parent.getWidth() - insets.left - insets.right;
				int h = parent.getHeight() - insets.top - insets.bottom;
				c.setBounds((int) (w * bounds.x) + insets.left, (int) (h * bounds.y) + insets.top, 
						(int) (w * bounds.width), (int) (h * bounds.height));
			}
			// "normal" components will not be laid out
		}
	}

}
