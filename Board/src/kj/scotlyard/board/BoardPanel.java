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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * Stellt das Spielbrett mit Stationen und Spielfiguren dar.
 * Dazu wird ein <code>Image</code> angezeigt, das erst 
 * gesetzt werden darf, wenn es vollständig geladen ist.
 * @author jakob190590
 *
 */
@SuppressWarnings("serial")
public class BoardPanel extends JPanel {
	
	private Image image;
	
	private Dimension preferredSize = new Dimension();
	
	@Override
	protected void paintComponent(Graphics g) {		
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		
		if (image != null) {
			g2D.drawImage(image, 0, 0, getWidth(), getHeight(), this);			
		}		
	}
	
	
	public Image getImage() {
		return image;
	}

	public void setImage(Image boardImage) {
		image = boardImage;
		preferredSize = new Dimension(image.getWidth(null), image.getHeight(null));
//		System.out.println(preferredSize);
	}


	@Override
	public Dimension getPreferredSize() {		
		return preferredSize;	
	}

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		this.preferredSize = preferredSize;
	}
	
}
