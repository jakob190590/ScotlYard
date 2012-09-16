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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;

import kj.scotlyard.game.control.GameController;
import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.control.impl.DefaultGameController;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.DefaultGame;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.TheRules;
import kj.scotlyard.game.util.MoveProducer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;

@SuppressWarnings("serial")
public class Board extends JFrame {

	private JPanel contentPane;
	
	GameController gc;
	Game g;
	GameGraph gg;
	
	private final Action newGameAction = new NewGameAction();
	private final Action clearPlayersAction = new ClearPlayersAction();
	private final Action newMrXAction = new NewMrXAction();
	private final Action newDetectiveAction = new NewDetectiveAction();
	private final Action startAction = new StartAction();
	private final Action abortAction = new AbortAction();
	private final Action moveAction = new MoveAction();
	private final Action gameStatusWinAction = new GameStatusWinAction();
	private final Action removeDetectiveAction = new RemoveDetectiveAction();
	private final Action shiftDetectiveUpAction = new ShiftDetectiveUpAction();
	private final Action shiftDetectiveDownAction = new ShiftDetectiveDownAction();
	private final Action newGameWithPlayerAction = new NewGameWithPlayerAction();


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
		setBounds(100, 100, 513, 321);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnGameController = new JMenu("GameController");
		mnGameController.setMnemonic('g');
		menuBar.add(mnGameController);
		
		JMenuItem mntmNewGame = new JMenuItem("New Game");
		mntmNewGame.setAction(newGameAction);
		mnGameController.add(mntmNewGame);
		
		JMenuItem mntmNewGameWith = new JMenuItem("New Game with Player");
		mntmNewGameWith.setAction(newGameWithPlayerAction);
		mnGameController.add(mntmNewGameWith);
		
		mnGameController.addSeparator();
		
		JMenuItem mntmNewMrx = new JMenuItem("New MrX");
		mntmNewMrx.setAction(newMrXAction);
		mnGameController.add(mntmNewMrx);
		
		JMenuItem mntmNewDetective = new JMenuItem("New Detective");
		mntmNewDetective.setAction(newDetectiveAction);
		mnGameController.add(mntmNewDetective);
		
		JMenu mnDetectives = new JMenu("Detectives");
		mnDetectives.setMnemonic('v');
		mnGameController.add(mnDetectives);
		
		JMenuItem mntmRemoveDetective = new JMenuItem("Remove Detective...");
		mntmRemoveDetective.setAction(removeDetectiveAction);
		mnDetectives.add(mntmRemoveDetective);
		
		mnDetectives.addSeparator();
		
		JMenuItem mntmShiftDetectiveUp = new JMenuItem("Shift Detective Up...");
		mntmShiftDetectiveUp.setAction(shiftDetectiveUpAction);
		mnDetectives.add(mntmShiftDetectiveUp);
		
		JMenuItem mntmShiftDetectiveDown = new JMenuItem("Shift Detective Down...");
		mntmShiftDetectiveDown.setAction(shiftDetectiveDownAction);
		mnDetectives.add(mntmShiftDetectiveDown);
		
		JMenuItem mntmClearPlayers = new JMenuItem("Clear Players");
		mntmClearPlayers.setAction(clearPlayersAction);
		mnGameController.add(mntmClearPlayers);
		
		mnGameController.addSeparator();
		
		JMenuItem mntmStart = new JMenuItem("Start");
		mntmStart.setAction(startAction);
		mnGameController.add(mntmStart);
		
		JMenuItem mntmAbort = new JMenuItem("Abort");
		mntmAbort.setAction(abortAction);
		mnGameController.add(mntmAbort);
		
		mnGameController.addSeparator();
		
		JMenuItem mntmMove = new JMenuItem("Move...");
		mntmMove.setAction(moveAction);
		mnGameController.add(mntmMove);
		
		mnGameController.addSeparator();
		
		JMenuItem mntmGameStatusAnd = new JMenuItem("Game Status and Win...");
		mntmGameStatusAnd.setAction(gameStatusWinAction);
		mnGameController.add(mntmGameStatusAnd);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		// Board laden
		BoardGraphLoader bgl = new BoardGraphLoader();
		try {
			bgl.load("graph-description", "initial-stations");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		JPanel boardPanelContainer = new JPanel(new AspectRatioGridLayout());
		BoardPanel board = new BoardPanel();
		
		MouseListener ml = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				JOptionPane.showMessageDialog(Board.this, "Klick auf VisualStation: " + e.getSource());
			}
		};
//		board.addMouseListener(ml);
		for (JComponent c : bgl.getVisualComponents()) {
			board.add(c);
			c.addMouseListener(ml);
		}
		board.buildVisualStationMap();
		
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
		
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		if (w < 0 || h < 0) {
			throw new IllegalArgumentException("The image seems to be not loaded " +
					"completely: Cannot determine image's width and/or height.");
		}
		board.setImage(img);	
		board.setPreferredSize(new Dimension(w, h));
		
		// GameState
		g = new DefaultGame();
		gg = bgl.getGameGraph();
		gc = new DefaultGameController(g, gg, new TheRules());
		gc.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				GameController c = (GameController) o;
				showGameStatusAndWin(c.getStatus(), c.getWin());
			}
		});
		board.setGameState(g);
		
		boardPanelContainer.add(board);
		
		contentPane.add(boardPanelContainer, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		JLabel lblGamecontroller = new JLabel("GameController");
		panel.add(lblGamecontroller);
		
		JButton btnNewGame = new JButton("New Game");
		btnNewGame.setAction(newGameAction);
		panel.add(btnNewGame);
		
		JButton btnClearplayers = new JButton("ClearPlayers");
		btnClearplayers.setAction(clearPlayersAction);
		panel.add(btnClearplayers);
		
		JButton btnStart = new JButton("Start");
		btnStart.setAction(startAction);
		panel.add(btnStart);
		
		JButton btnAbort = new JButton("Abort");
		btnAbort.setAction(abortAction);
		panel.add(btnAbort);
		
		JButton btnMove = new JButton("Move...");
		btnMove.setAction(moveAction);
		panel.add(btnMove);
		
		
//		pack();
	}

	private class NewGameAction extends AbstractAction {
		public NewGameAction() {
			putValue(NAME, "New Game");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_N);
		}
		public void actionPerformed(ActionEvent e) {
			gc.newGame();
		}
	}
	private class ClearPlayersAction extends AbstractAction {
		public ClearPlayersAction() {
			putValue(NAME, "Clear Players");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_C);
		}
		public void actionPerformed(ActionEvent e) {
			gc.clearPlayers();
		}
	}
	private class NewMrXAction extends AbstractAction {
		public NewMrXAction() {
			putValue(NAME, "New MrX");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_X);
		}
		public void actionPerformed(ActionEvent e) {
			gc.newMrX();
		}
	}
	private class NewDetectiveAction extends AbstractAction {
		public NewDetectiveAction() {
			putValue(NAME, "New Detective");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_D);
		}
		public void actionPerformed(ActionEvent e) {
			gc.newDetective();
		}
	}
	private class StartAction extends AbstractAction {
		public StartAction() {
			putValue(NAME, "Start");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_S);
		}
		public void actionPerformed(ActionEvent e) {
			try {
				gc.start();
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(Board.this, e2.getMessage(), 
						e2.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	private class AbortAction extends AbstractAction {
		public AbortAction() {
			putValue(NAME, "Abort");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_A);
		}
		public void actionPerformed(ActionEvent e) {
			gc.abort();
		}
	}
	private class MoveAction extends AbstractAction {
		public MoveAction() {
			putValue(NAME, "Move...");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_M);
		}
		public void actionPerformed(ActionEvent e) {
			try {
				Move move = null;
				move = MoveProducer.createNextBestSingleMove(g, gg);
				gc.move(move);
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(Board.this, e2.getMessage(), 
						e2.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	private class GameStatusWinAction extends AbstractAction {
		public GameStatusWinAction() {
			putValue(NAME, "Game Status and Win...");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_I); // i as information
		}
		public void actionPerformed(ActionEvent e) {
			showGameStatusAndWin(gc.getStatus(), gc.getWin());
		}
	}
	private class RemoveDetectiveAction extends AbstractAction {
		public RemoveDetectiveAction() {
			putValue(NAME, "Remove Detective...");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_R);
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class ShiftDetectiveUpAction extends AbstractAction {
		public ShiftDetectiveUpAction() {
			putValue(NAME, "Shift Detective Up...");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_U);
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class ShiftDetectiveDownAction extends AbstractAction {
		public ShiftDetectiveDownAction() {
			putValue(NAME, "Shift Detective Down...");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_D);
			putValue(DISPLAYED_MNEMONIC_INDEX_KEY, 16);
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
	private class NewGameWithPlayerAction extends AbstractAction {
		public NewGameWithPlayerAction() {
			putValue(NAME, "New Game with Player");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_W);
		}
		public void actionPerformed(ActionEvent e) {
			gc.clearPlayers();
			gc.newMrX();
			for (int i = 0; i < 4; i++)
				gc.newDetective();
			
			gc.newGame();
		}
	}
	private void showGameStatusAndWin(GameStatus status, GameWin win) {
		JOptionPane.showMessageDialog(Board.this, String.format(
				"Status: %s\nWin: %s", status, win));
	}
}
