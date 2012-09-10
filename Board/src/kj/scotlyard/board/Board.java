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

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;

@SuppressWarnings("serial")
public class Board extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Board frame = new Board();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Board() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel boardPanelContainer = new JPanel(new AspectRatioGridLayout());
		BoardPanel board = new BoardPanel();
		
		Image img = null;
		// Variante 1
		try {
			img = ImageIO.read(new File("original-scotland-yard-board.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Variante 2 (ungefaehr)
//		img = Toolkit.getDefaultToolkit().getImage("original-scotland-yard-board.png");
//		private final ImageObserver imageSizeObserver = new ImageObserver() {
//			@Override
//			public boolean imageUpdate(Image img, int infoflags, int x, int y,
//					int width, int height) {
//				// width und height koennten einzeln oder gleichzeitig ankommen
//				if ((infoflags & WIDTH) != 0) {				
//					imageWidth = width;
//				}
//				if ((infoflags & HEIGHT) != 0) {				
//					imageHeight = height;
//				}
//				if ((imageWidth >= 0) && (imageHeight >= 0)) {
//					preferredSize = new Dimension(imageWidth, imageHeight);
//					return false; // required information has been acquired
//				}
//				return true; // further updates are needed
//			}
//		};
//		imageWidth = image.getWidth(imageSizeObserver);
//		imageHeight = image.getHeight(imageSizeObserver);
//		if ((imageWidth >= 0) && (imageHeight >= 0)) {
//			preferredSize = new Dimension(imageWidth, imageHeight);
//		}		
		board.setImage(img);		
		boardPanelContainer.add(board);
		
		contentPane.add(boardPanelContainer, BorderLayout.CENTER);
		
		
//		pack();
	}

}
