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
import java.awt.GridLayout;
import java.awt.Insets;

/**
 * Lays out the components like GridLayout, but centers each 
 * component in it's rectangle, preserving the aspect ratio 
 * defined by PreferredSize.
 * 
 * @author jakob190590
 *
 */
@SuppressWarnings("serial")
public class AspectRatioGridLayout extends GridLayout {

	public AspectRatioGridLayout() {
		super();
	}

	public AspectRatioGridLayout(int rows, int cols, int hgap, int vgap) {
		super(rows, cols, hgap, vgap);
	}

	public AspectRatioGridLayout(int rows, int cols) {
		super(rows, cols);
	}

	private void setAspectRatioBounds(Component component, int x, int y, int w,
			int h) {

		Dimension size = component.getPreferredSize();
		double ar = (double) size.width / size.height; // aspect ratio

		double hxar = h * ar;
		if (hxar > w) {
			// -> auf breite anpassen
			int nh = (int) (w / ar);
			component.setBounds(x, y + (h - nh) / 2, w, nh);
		} else {
			// -> auf hoehe anpassen
			int nw = (int) hxar;
			component.setBounds(x + (w - nw) / 2, y, nw, h);
		}

//		component.setBounds(x, y, w, h);

	}
	
	
	@Override
	public void layoutContainer(Container parent) {
		int hgap = getHgap();
		int vgap = getVgap();

		// TODO Verdammt, das erinnert mich an den Code von GridLayout (c)   :(
		// Aber was soll ich machen? Ich brauch' ja genau den gleichen Algo ...
		synchronized (parent.getTreeLock()) {			
			Insets insets = parent.getInsets();
			int componentCount = parent.getComponentCount();
			int rowCount = getRows();
			int colCount = getColumns();
			boolean ltr = parent.getComponentOrientation().isLeftToRight();

			if (componentCount != 0) {

				if (rowCount > 0) {
					colCount = (componentCount + rowCount - 1) / rowCount;
				} else {
					rowCount = (componentCount + colCount - 1) / colCount;
				}

				int totalGapsWidth = (colCount - 1) * hgap;
				int widthMinusInsets = parent.getWidth()
						- (insets.left + insets.right);
				int widthOnComponent = (widthMinusInsets - totalGapsWidth) / colCount;
				int extraWidthAvailable = (widthMinusInsets - (widthOnComponent
						* colCount + totalGapsWidth)) / 2;

				int totalGapsHeight = (rowCount - 1) * vgap;
				int heightMinusInsets = parent.getHeight()
						- (insets.top + insets.bottom);
				int heightOnComponent = (heightMinusInsets - totalGapsHeight)
						/ rowCount;
				int extraHeightAvailable = (heightMinusInsets - (heightOnComponent
						* rowCount + totalGapsHeight)) / 2;
				if (ltr) {
					for (int c = 0, x = insets.left + extraWidthAvailable; c < colCount; c++, x += widthOnComponent
							+ hgap) {
						for (int r = 0, y = insets.top + extraHeightAvailable; r < rowCount; r++, y += heightOnComponent
								+ vgap) {
							int i = r * colCount + c;
							if (i < componentCount) {
								setAspectRatioBounds(parent.getComponent(i), x,
										y, widthOnComponent, heightOnComponent);
							}
						}
					}
				} else {
					for (int c = 0, x = (parent.getWidth() - insets.right - widthOnComponent)
							- extraWidthAvailable; c < colCount; c++, x -= widthOnComponent
							+ hgap) {
						for (int r = 0, y = insets.top + extraHeightAvailable; r < rowCount; r++, y += heightOnComponent
								+ vgap) {
							int i = r * colCount + c;
							if (i < componentCount) {
								setAspectRatioBounds(parent.getComponent(i), x,
										y, widthOnComponent, heightOnComponent);
							}
						}
					}
				}
			}
		}
	}

}
