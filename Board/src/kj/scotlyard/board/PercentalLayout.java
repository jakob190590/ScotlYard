package kj.scotlyard.board;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.geom.Rectangle2D;

/**
 * A layout manager that behaves like the <code>null</code> layout
 * for normal components, but lays out <code>PercentalBounds</code>
 * objects according to it's location and size hints.
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void layoutContainer(Container parent) {
		// TODO Auto-generated method stub
		for (Component c : parent.getComponents()) {
			if (c instanceof PercentalBounds) {
				PercentalBounds pb = (PercentalBounds) c;
				Rectangle2D.Double bounds = pb.getBounds2();
				int w = parent.getWidth();
				int h = parent.getHeight();
				c.setBounds((int) (w * bounds.x), (int) (h * bounds.y), 
						(int) (w * bounds.width), (int) (h * bounds.height));
			}
			// "normal" components will be ignored
		}
	}

}
