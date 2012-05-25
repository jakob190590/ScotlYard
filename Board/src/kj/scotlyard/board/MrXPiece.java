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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import kj.scotlyard.game.model.MrXPlayer;

@SuppressWarnings("serial")
public class MrXPiece extends Piece {

	public MrXPiece(MrXPlayer player, String playerName) {
		super(player, playerName);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		
		g.setColor(Color.BLACK);
		g2D.drawOval(getX() - getHalfWidth(), getY() - getHalfHeight(),
				getHalfWidth(), getHalfHeight());
	}
	
}
