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

import static kj.scotlyard.board.ActionTools.isSelected;
import static kj.scotlyard.board.ActionTools.setSelected;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.undo.UndoManager;

import kj.scotlyard.board.board.BoardPanel;
import kj.scotlyard.board.layout.AspectRatioGridLayout;
import kj.scotlyard.board.metadata.GameMetaData;
import kj.scotlyard.game.ai.detective.DetectiveAi;
import kj.scotlyard.game.ai.mrx.MrXAi;
import kj.scotlyard.game.control.GameController;
import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.control.impl.DefaultGameController;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DefaultGame;
import kj.scotlyard.game.model.DefaultGameState;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MoveListener;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.TurnListener;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.rules.TheRules;
import kj.scotlyard.game.util.MoveProducer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

@SuppressWarnings("serial")
public class Board extends JFrame {
	
	private static final Logger logger = Logger.getLogger(Board.class);
	
	/** Faktor, mit dem zoomFactor bei jedem Zoom-Schritt verrechnet wird (mal/geteilt) */
	private static final double ZOMMING_FACTOR = 1.2;
	
	private Image image;

	private JPanel contentPane;
	
	private BoardPanel boardPanel;
	
	private JLabel lblCurrentRoundNumberVal;
	
	private JLabel lblCurrentPlayerVal;
	
	private JLabel lblMoveVal;
	
	private JPanel boardPanelContainer;
	
	private JScrollPane boardPanelScrollPane;
	
	private MovePreparationBar movePreparationBar;
	
	private final ButtonGroup modeButtonGroup = new ButtonGroup();
	
	UndoManager undoManager = new UndoManager();
	
	private Rules rules;
	private GameController gameController;
	private Game game;
	private GameState gameState;
	private GameGraph gameGraph;
	private Map<Integer, StationVertex> numberStationMap;
	private MrXAi mrXAi;
	private DetectiveAi detectiveAi;
	
	private MovePreparer movePreparer;
	
	private Dimension originalImageSize;
	
	private TicketSelectionDialog ticketSelectionDialogMrX = new TicketSelectionDialog(this, MrXPlayer.class);
	private TicketSelectionDialog ticketSelectionDialogDetectives = new TicketSelectionDialog(this, DetectivePlayer.class);
	
	private double normalZoomFactor = 0.2; // kann sein was will, nur >= 1 macht keinen sinn, weil das bild dann viel zu riessig ist!
	private double zoomFactor = normalZoomFactor;
	
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
	private final Action newGameWithPlayersAction = new NewGameWithPlayersAction();
	private final Action undoAction = new UndoAction();
	private final Action redoAction = new RedoAction();
	private final Action suggestMoveAction = new SuggestMoveAction();
	private final Action moveNowAction = new MoveNowAction();
	private final Action moveDetectivesNowAction = new MoveDetectivesNowAction();
	private final Action quickPlayAction = new QuickPlayAction();
	private final Action fitBoardAction = new FitBoardAction();
	private final Action zoomInAction = new ZoomInAction();
	private final Action zoomOutAction = new ZoomOutAction();
	private final Action zoomNormalAction = new ZoomNormalAction();
	private final Action selectCurrentPlayerAction = new SelectCurrentPlayerAction();
	private final Action jointMovingAction = new JointMovingAction();
	private final Action mrXAlwaysVisibleAction = new MrXAlwaysVisibleAction();

	private final MoveListener moveListener = new MoveListener() {
		@Override
		public void movesCleard(GameState gameState) {
			movePreparer.resetAll();
		}
		@Override
		public void moveUndone(GameState gameState, Move move) {
			logger.info(String.format("move undone; player: %s", move.getPlayer()));
			movePreparer.reset(move.getPlayer());
		}
		@Override
		public void moveDone(GameState gameState, Move move) {
			logger.info(String.format("move done; player: %s", move.getPlayer()));
//			mPrep.reset(move.getPlayer());
			// Unnoetig, da zum Rundenwechsel eh resetAll() aufgerufen wird
			// Stoerend, dadurch das (nicht ganz zuverlaessige) counting nicht funktioniert (MoveDetectivesNowAction)
		}
	};

	private final TurnListener turnListener = new TurnListener() {
		@Override
		public void currentRoundChanged(GameState gameState, int oldRoundNumber,
				int newRoundNumber) {
			// Beim Wechsel in naechste Runde
			logger.info(String.format("round changed: %d -> %d", oldRoundNumber, newRoundNumber));
			
			lblCurrentRoundNumberVal.setText(String.valueOf(newRoundNumber));
			
			movePreparer.resetAll();
		}
		@Override
		public void currentPlayerChanged(GameState gameState, Player oldPlayer,
				Player newPlayer) {
			lblCurrentPlayerVal.setText((newPlayer == null) ? "(None)"
					: GameMetaData.getForPlayer(newPlayer).getName());

			// TODO nicht fuer current, sondern fuer selected player
			Move m = movePreparer.getMove(newPlayer);
			lblMoveVal.setText((m == null) ? "Noch kein Move vorbereitet" : m.toString());
		}
	};

	private final Observer gameControllerObserver = new Observer() {
		@Override
		public void update(Observable o, Object arg) {
			GameController c = (GameController) o;
			
			logger.info(String.format("state changed: %s, %s", c.getStatus(), c.getWin()));
			
			movePreparationBar.setEnabled(c.getStatus() == GameStatus.IN_GAME);
			setGameControllerActionsEnabled(c.getStatus());
			if (c.getStatus() != GameStatus.IN_GAME)
				showGameStatusAndWin(c.getStatus(), c.getWin());
		}
	};
	private final Action aboutAction = new AboutAction();
	private HistoryPanel historyPanel;



	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Board frame = new Board();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		PropertyConfigurator.configure("log4j.properties");
	}

	/**
	 * Create the frame.
	 */
	public Board() {
		
		createController();
		
		setTitle("ScotlYard");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 600);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnGameController = new JMenu("GameController");
		mnGameController.setMnemonic(KeyEvent.VK_C);
		menuBar.add(mnGameController);
		
		JMenuItem mntmNewGame = new JMenuItem("New Game");
		mntmNewGame.setAction(newGameAction);
		mnGameController.add(mntmNewGame);
		
		JMenuItem mntmNewGameWith = new JMenuItem("New Game with Player");
		mntmNewGameWith.setAction(newGameWithPlayersAction);
		mnGameController.add(mntmNewGameWith);
		
		mnGameController.addSeparator();
		
		JMenuItem mntmNewMrx = new JMenuItem("New MrX");
		mntmNewMrx.setAction(newMrXAction);
		mnGameController.add(mntmNewMrx);
		
		JMenuItem mntmNewDetective = new JMenuItem("New Detective");
		mntmNewDetective.setAction(newDetectiveAction);
		mnGameController.add(mntmNewDetective);
		
		JMenu mnDetectives = new JMenu("Detectives");
		mnDetectives.setMnemonic(KeyEvent.VK_V);
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
		
		JMenu mnUndoRedo = new JMenu("Undo/Redo");
		mnUndoRedo.setMnemonic(KeyEvent.VK_U);
		mnGameController.add(mnUndoRedo);
		
		JMenuItem mntmUndo = new JMenuItem("Undo");
		mntmUndo.setAction(undoAction);
		mnUndoRedo.add(mntmUndo);
		
		JMenuItem mntmRedo = new JMenuItem("Redo");
		mntmRedo.setAction(redoAction);
		mnUndoRedo.add(mntmRedo);
		
		mnGameController.addSeparator();
		
		JMenuItem mntmGameStatusAnd = new JMenuItem("Game Status and Win...");
		mntmGameStatusAnd.setAction(gameStatusWinAction);
		mnGameController.add(mntmGameStatusAnd);
		
		
		JMenu mnSetter = new JMenu("Setter");
		mnSetter.setMnemonic(KeyEvent.VK_S);
		menuBar.add(mnSetter);
		
		// Um zu ueberpruefen, ob das neu setzen waehrend dem Spiel funktioniert
		JMenuItem mntmSetGameStateNull = new JMenuItem("Set GameState to null");
		mntmSetGameStateNull.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setGameState(null);
			}
		});
		mntmSetGameStateNull.setMnemonic(KeyEvent.VK_N);
		mnSetter.add(mntmSetGameStateNull);
		
		JMenuItem mntmSetGameState = new JMenuItem("Set GameState");
		mntmSetGameState.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setGameState(gameState);
			}
		});
		mntmSetGameState.setMnemonic(KeyEvent.VK_G);
		mnSetter.add(mntmSetGameState);
		
		mnSetter.addSeparator();
		
		JMenuItem mntmSetRulesNull = new JMenuItem("Set Rules to null");
		mntmSetRulesNull.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setRules(null);
			}
		});
		mntmSetRulesNull.setMnemonic(KeyEvent.VK_U);
		mnSetter.add(mntmSetRulesNull);
		
		JMenuItem mntmSetRules = new JMenuItem("Set Rules");
		mntmSetRules.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setRules(rules);
			}
		});
		mntmSetRules.setMnemonic(KeyEvent.VK_R);
		mnSetter.add(mntmSetRules);
		
		mnSetter.addSeparator();
		
		JMenuItem mntmSetImageToNull = new JMenuItem("Set Image to null");
		mntmSetImageToNull.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setImage(null);
			}
		});
		mntmSetImageToNull.setMnemonic(KeyEvent.VK_L);
		mnSetter.add(mntmSetImageToNull);
		
		JMenuItem mntmSetImage = new JMenuItem("Set Image");
		mntmSetImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setImage(image);
			}
		});
		mntmSetImage.setMnemonic(KeyEvent.VK_I);
		mnSetter.add(mntmSetImage);
		
		JMenu mnErnsthaft = new JMenu("Ernsthaft");
		menuBar.add(mnErnsthaft);
		
		JCheckBoxMenuItem mntmQuickPlay = new JCheckBoxMenuItem("Quick Play");
		mntmQuickPlay.setAction(quickPlayAction);
		mnErnsthaft.add(mntmQuickPlay);
		
		JCheckBoxMenuItem chckbxmntmJointMoving = new JCheckBoxMenuItem("Joint Moving");
		chckbxmntmJointMoving.setAction(jointMovingAction);
		mnErnsthaft.add(chckbxmntmJointMoving);
		
		JMenuItem mntmShowMrx = new JMenuItem("Show MrX");
		mnErnsthaft.add(mntmShowMrx);
		
		JMenu mnZoom = new JMenu("Zoom");
		mnZoom.setMnemonic(KeyEvent.VK_Z);
		mnErnsthaft.add(mnZoom);
		
		JCheckBoxMenuItem chckbxmntmFitBoard = new JCheckBoxMenuItem("Fit Board");
		chckbxmntmFitBoard.setAction(fitBoardAction);
		mnZoom.add(chckbxmntmFitBoard);
		
		JMenuItem mntmZoomIn = new JMenuItem("Zoom In");
		mntmZoomIn.setAction(zoomInAction);
		mnZoom.add(mntmZoomIn);
		
		JMenuItem mntmZoomOut = new JMenuItem("Zoom Out");
		mntmZoomOut.setAction(zoomOutAction);
		mnZoom.add(mntmZoomOut);
		
		JMenuItem mntmZoomNormal = new JMenuItem("Zoom Normal");
		mntmZoomNormal.setAction(zoomNormalAction);
		mnZoom.add(mntmZoomNormal);
		
		JMenu mnMode = new JMenu("Mode");
		mnMode.setMnemonic(KeyEvent.VK_M);
		menuBar.add(mnMode);
		
		JRadioButtonMenuItem mntmNormalGame = new JRadioButtonMenuItem("Normal Game");
		modeButtonGroup.add(mntmNormalGame);
		mnMode.add(mntmNormalGame);
		
		// Besser, wenn NormalGame standardmäßig auch Server wäre
		JRadioButtonMenuItem mntmBeServer = new JRadioButtonMenuItem("Be Server");
		modeButtonGroup.add(mntmBeServer);
		mnMode.add(mntmBeServer);
		
		// Connect to Server müsste eigentlich eigene Funktion sein
		JRadioButtonMenuItem mntmBeClient = new JRadioButtonMenuItem("Be Client / Connect to Server");
		modeButtonGroup.add(mntmBeClient);
		mnMode.add(mntmBeClient);
		
		// Modus zur Hilfe fuer Detectives, wenn man am echten Spielbrett spielt: Man muss nicht fuer MrX ziehen, sondern nur seine Tickets eingeben
		JRadioButtonMenuItem mntmMrXTracking = new JRadioButtonMenuItem("MrX Tracking");
		modeButtonGroup.add(mntmMrXTracking);
		mnMode.add(mntmMrXTracking);
		
		mnMode.addSeparator();
		
		JCheckBoxMenuItem mntmMrXAlwaysVisible = new JCheckBoxMenuItem("MrX Always Visible");
		mntmMrXAlwaysVisible.setAction(mrXAlwaysVisibleAction);
		mnMode.add(mntmMrXAlwaysVisible);
		
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic(KeyEvent.VK_H);
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.setAction(aboutAction);
		mnHelp.add(mntmAbout);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		boardPanelContainer = new JPanel(new AspectRatioGridLayout());
		boardPanel = new BoardPanel();
		boardPanel.setMovePreparer(movePreparer);
		
		boardPanelContainer.add(boardPanel);
		// mit preferredSize ist es so:
		// 1. vorher nicht gesetzt heisst: nimm' vom layoutmanager
		// 2. setzen heisst: nimm' ab jetzt nur noch das angegebene
		// 3. wieder auf null setzen heisst: nicht gesetzt, siehe 1.
		boardPanelContainer.setPreferredSize(new Dimension());
		
		boardPanelScrollPane = new JScrollPane(boardPanelContainer);
		// Scrollgeschwindigkeit einstellen
		final int scrollBarUnitIncrement = 30;
		boardPanelScrollPane.getVerticalScrollBar().setUnitIncrement(scrollBarUnitIncrement);
		boardPanelScrollPane.getHorizontalScrollBar().setUnitIncrement(scrollBarUnitIncrement);
		
		contentPane.add(boardPanelScrollPane, BorderLayout.CENTER);
		
		JPanel toolbarContainer = new JPanel();
		contentPane.add(toolbarContainer, BorderLayout.NORTH);
		toolbarContainer.setLayout(new BoxLayout(toolbarContainer, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		toolbarContainer.add(panel);
		
		JLabel lblGameController = new JLabel("GameController");
		panel.add(lblGameController);
		
		JButton btnNewGameWithPlayers = new JButton("New Game with Players");
		btnNewGameWithPlayers.setAction(newGameWithPlayersAction);
		panel.add(btnNewGameWithPlayers);
		
		JButton btnStart = new JButton("Start");
		btnStart.setAction(startAction);
		panel.add(btnStart);
		
		JButton btnAbort = new JButton("Abort");
		btnAbort.setAction(abortAction);
		panel.add(btnAbort);
		
		JButton btnMoveM = new JButton("Move...");
		btnMoveM.setAction(moveAction);
		panel.add(btnMoveM);
		
		movePreparationBar = new MovePreparationBar();
		movePreparationBar.setMovePreparer(movePreparer);
		toolbarContainer.add(movePreparationBar);
		
		JPanel moveControlBar = new JPanel();
		toolbarContainer.add(moveControlBar);
		
		lblMoveVal = new JLabel("MoveVal");
		moveControlBar.add(lblMoveVal);
		
		JButton btnMove = new JButton("Move!");
		btnMove.setAction(moveNowAction);
		moveControlBar.add(btnMove);
		
		JButton btnSuggestMove = new JButton("Suggest Move");
		btnSuggestMove.setAction(suggestMoveAction);
		moveControlBar.add(btnSuggestMove);
		
		JButton btnMoveRound = new JButton("Move Detectives!");
		btnMoveRound.setAction(moveDetectivesNowAction);
		moveControlBar.add(btnMoveRound);
		
		JPanel panelLeft = new JPanel();
		contentPane.add(panelLeft, BorderLayout.WEST);
		panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
		
		JLabel lblCurrentRoundNumber = new JLabel("Current Round Number");
		lblCurrentRoundNumber.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelLeft.add(lblCurrentRoundNumber);
		
		lblCurrentRoundNumberVal = new JLabel("R#");
		lblCurrentRoundNumberVal.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblCurrentRoundNumber.setLabelFor(lblCurrentRoundNumberVal);
		lblCurrentRoundNumberVal.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrentRoundNumberVal.setFont(new Font("Tahoma", Font.BOLD, 36));
		panelLeft.add(lblCurrentRoundNumberVal);
		
		JLabel lblSeparator = new JLabel(" ");
		lblSeparator.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelLeft.add(lblSeparator);
		
		JLabel lblCurrentPlayer = new JLabel("Current Player");
		lblCurrentPlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelLeft.add(lblCurrentPlayer);
		
		lblCurrentPlayerVal = new JLabel("CurrentPlayerVal");
		lblCurrentPlayer.setLabelFor(lblCurrentPlayerVal);
		lblCurrentPlayerVal.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblCurrentPlayerVal.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectCurrentPlayerAction.actionPerformed(null);
			}
		});
		panelLeft.add(lblCurrentPlayerVal);
		
		historyPanel = new HistoryPanel();
		contentPane.add(historyPanel, BorderLayout.SOUTH);
		
		
		
		
		// Daten/Models/... laden bzw. erzeugen
				
		loadRules();
		loadBoard();
		loadGame();
		loadMrXAi();
		loadDetectiveAi();
		
//		pack();
	}

	private void createController() {
		// Controll Komponenten
		movePreparer = new MovePreparer() {
			@Override
			protected void errorSelectingPlayer(Player player) {
				logger.error("dieser player kann (jetzt) nicht ausgewaehlt werden");
				UIManager.getLookAndFeel().provideErrorFeedback(null);
			}
			
			@Override
			protected void errorImpossibleNextStation(StationVertex station,
					Player player) {
				logger.error("impossible next station for: " + player);
				UIManager.getLookAndFeel().provideErrorFeedback(null);
			}

			@Override
			protected Ticket selectTicket(Set<Ticket> tickets, Player player) {
				if (player instanceof MrXPlayer)
					return ticketSelectionDialogMrX.show(tickets, player);
				else // DetectivePlayer
					return ticketSelectionDialogDetectives.show(tickets, player);
//				return tickets.iterator().next(); // gleich das erste
			}
		};
		movePreparer.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				MovePreparationEvent mpe;
				if (arg instanceof MovePreparationEvent && (mpe = (MovePreparationEvent) arg)
						.getId() == MovePreparationEvent.NEXT_STATION) {
					
					logger.debug("move prepared");
					Player player = mpe.getPlayer();
					
					if (player == gameState.getCurrentPlayer()) {
						lblMoveVal.setText(mpe.getMove().toString());
					}
					
					boolean furtherMoves = (player instanceof MrXPlayer) ?
							ticketSelectionDialogMrX.isFurtherMovesSelected() : false;
					if (isSelected(quickPlayAction)
							&& player == gameState.getCurrentPlayer() // selected player's turn
							&& !furtherMoves) { // no further moves
						logger.debug("Move fertig vorbereitet, QuickPlay, no further moves and Turn");
						move(mpe.getMove());
					}
				} else {
					logger.debug("player selected");
				}
			}
		});
	}
	
	
	
	private void showErrorMessage(Exception e) {
		JOptionPane.showMessageDialog(this, e.getMessage(), e.getClass()
				.getSimpleName(), JOptionPane.ERROR_MESSAGE);
	}
	private void showGameStatusAndWin(GameStatus status, GameWin win) {
		JOptionPane.showMessageDialog(Board.this, String.format(
				"Status: %s\nWin: %s", status, win));
	}
	private void setGameControllerActionsEnabled(GameStatus status) {
		boolean inGame = (status == GameStatus.IN_GAME);
		newGameAction.setEnabled(!inGame);
		//clearPlayersAction.setEnabled(inGame);
		//newMrXAction.setEnabled(inGame);
		//newDetectiveAction.setEnabled(inGame);
		startAction.setEnabled(!inGame);
		abortAction.setEnabled(inGame);
		moveAction.setEnabled(inGame);
		
		//removeDetectiveAction.setEnabled(inGame);
		//shiftDetectiveUpAction.setEnabled(inGame);
		//shiftDetectiveDownAction.setEnabled(inGame);
		newGameWithPlayersAction.setEnabled(!inGame);
	}
	private void updateBoardPanelZoom() {
		int w = (int) (originalImageSize.width * zoomFactor);
		int h = (int) (originalImageSize.height * zoomFactor);
		boardPanelContainer.setPreferredSize(new Dimension(w, h));
		boardPanelScrollPane.getViewport().revalidate();
		boardPanelScrollPane.getViewport().repaint();
		
		// Zusatz, dass nicht kleiner gezoomt werden kann, als das Fenster ist:
		if (w < boardPanelScrollPane.getViewport().getWidth()
				&& h < boardPanelScrollPane.getViewport().getHeight()) {
			w = boardPanelScrollPane.getViewport().getWidth();
			h = boardPanelScrollPane.getViewport().getHeight();
			boardPanelContainer.setPreferredSize(new Dimension(w, h));
		}
	}

	/**
	 * Try to carry out the specified move.
	 * When the MovePolicy throws an Exception,
	 * an error message dialog pops up.
	 * @param move
	 * @return <code>true</code> when successful
	 */
	private boolean move(Move move) {
		boolean success = true;
		try {
			logger.debug("try to carry out move for: " + move.getPlayer());
			gameController.move(move);
		} catch (Exception e2) {
			success = false;
			e2.printStackTrace();
			showErrorMessage(e2);
		}
		return success;
	}
	
	// --------------------------------------------------
	// Setter/Getter fuer meine Felder

	protected Rules getRules() {
		return rules;
	}

	protected void setRules(Rules rules) {
		this.rules = rules;
		boardPanel.setRules(rules);
		historyPanel.setRules(rules);
	}

	protected GameController getGameController() {
		return gameController;
	}

	protected void setGameController(GameController gameController) {
		this.gameController = gameController;
	}

	protected Game getGame() {
		return game;
	}

	protected void setGame(Game game) {
		this.game = game;
	}

	protected GameState getGameState() {
		return gameState;
	}

	protected void setGameState(GameState gameState) {
		if (gameState != this.gameState) {
			if (this.gameState != null) {
				// unregister listeners/observers
				gameState.removeMoveListener(moveListener);
				gameState.removeTurnListener(turnListener);
			}
			this.gameState = gameState;
			movePreparationBar.setGameState(gameState);
			boardPanel.setGameState(gameState);
			movePreparer.setGameState(gameState);
			historyPanel.setGameState(gameState);
			
			if (gameState != null) {
				// register listeners/observers
				gameState.addMoveListener(moveListener);
				gameState.addTurnListener(turnListener);
			}
		}
		
	}

	protected GameGraph getGameGraph() {
		return gameGraph;
	}

	protected void setGameGraph(GameGraph gameGraph) {
		this.gameGraph = gameGraph;
		movePreparer.setGameGraph(gameGraph);
		boardPanel.setGameGraph(gameGraph);
	}

	protected Map<Integer, StationVertex> getNumberStationMap() {
		return numberStationMap;
	}

	protected void setNumberStationMap(Map<Integer, StationVertex> numberStationMap) {
		this.numberStationMap = numberStationMap;
		movePreparationBar.setNumberStationMap(numberStationMap);
	}

	protected MrXAi getMrXAi() {
		return mrXAi;
	}

	protected void setMrXAi(MrXAi mrXAi) {
		this.mrXAi = mrXAi;
	}

	protected DetectiveAi getDetectiveAi() {
		return detectiveAi;
	}

	protected void setDetectiveAi(DetectiveAi detectiveAi) {
		this.detectiveAi = detectiveAi;
	}
	
	protected Image getImage() {
		return image;
	}
	
	protected void setImage(Image image) {
		this.image = image;
		boardPanel.setImage(image);
	}
	
	protected Dimension getOriginalImageSize() {
		return originalImageSize;
	}
	
	protected void setOriginalImageSize(Dimension originalImageSize) {
		this.originalImageSize = originalImageSize;
		boardPanel.setPreferredSize(originalImageSize);
	}
	
	// **************************************************
	// Alle benoetigten Daten/Objekte laden bzw. erzeugen
	

	/** Rules laden (spaeter auch aus JAR) */
	protected void loadRules(/* String jarName, String className */) {
		Rules rules;
//		if (jarName != null) {
//			// link JAR file
//		}
//		if (className != null) {
//			Class.forName(classname).createInstance();
//		} else {
		
			rules = new TheRules();
			
//		}
			
		setRules(rules);
	}

	/** Boarddef laden, d.h. GameGraph, Board */
	protected void loadBoard(/* params, wie boarddef filename */) {
		
		// Momentan wird noch das Standard Board geladen ...
		
		// Board laden
		BoardGraphLoader bgl = new BoardGraphLoader();
		try {
			bgl.load("graph-description", "initial-stations");
		} catch (IOException e1) {
			e1.printStackTrace();
			showErrorMessage(e1);
		}
		
		// Dem BoardPanel alle VisualComponents adden
		for (JComponent c : bgl.getVisualComponents()) {
			boardPanel.add(c);
		}
		boardPanel.buildVisualStationMap();
		
		Map<Integer, StationVertex> numberStationMap = bgl.getNumberStationMap();
		
		GameGraph gameGraph = bgl.getGameGraph();
		
		// TODO beide muessen beim laden der Board def gesetzt werden
		normalZoomFactor = 0.2; // kann sein was will, nur >= 1 macht keinen sinn, weil das bild dann viel zu riessig ist!
		zoomFactor = normalZoomFactor;
		
		// Bild laden
		
		Image img = null;
		try {
			img = ImageIO.read(new File("original-scotland-yard-board.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		if (w < 0 || h < 0) {
			throw new IllegalArgumentException("The image seems to be not loaded " +
					"completely: Cannot determine image's width and/or height.");
		}
		Dimension originalImageSize = new Dimension(w, h);
		
		
		
		setImage(img);
		setOriginalImageSize(originalImageSize);
		
		setGameGraph(gameGraph);
		setNumberStationMap(numberStationMap);
		
	}
	
	/** Game, GameState, GameController erzeugen */
	protected void loadGame(/* params wie gamestate filename*/) {
		
		Game game = new DefaultGame();
		GameState gameState = new DefaultGameState(game);
		
		undoManager.discardAllEdits(); // am besten erst, wenn erfolgreich geladen
		GameController gameController = new DefaultGameController(game, gameGraph, rules);
		gameController.setUndoManager(undoManager);
		gameController.addObserver(gameControllerObserver );
		
		setGameControllerActionsEnabled(gameController.getStatus());
		
		setGame(game);
		setGameState(gameState);
		setGameController(gameController);
		
	}
	
	/** MrX AI laden (spaeter auch aus JAR) */
	protected void loadMrXAi(/* params wie JAR und cassname*/) {
	}
	
	/** Detective AI laden (spaeter auch aus JAR) */
	protected void loadDetectiveAi(/* params wie JAR und classname*/) {
	}
	
	
	// Actions *******************************************
	
	
	private class NewGameAction extends AbstractAction {
		public NewGameAction() {
			putValue(NAME, "New Game");
			putValue(SHORT_DESCRIPTION, "Create a new game");
			putValue(MNEMONIC_KEY, KeyEvent.VK_N);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			gameController.newGame();
		}
	}
	private class ClearPlayersAction extends AbstractAction {
		public ClearPlayersAction() {
			putValue(NAME, "Clear Players");
			putValue(SHORT_DESCRIPTION, "Clear all players");
			putValue(MNEMONIC_KEY, KeyEvent.VK_C);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			gameController.clearPlayers();
		}
	}
	private class NewMrXAction extends AbstractAction {
		public NewMrXAction() {
			putValue(NAME, "New MrX");
			putValue(SHORT_DESCRIPTION, "Set new Mr. X");
			putValue(MNEMONIC_KEY, KeyEvent.VK_X);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			gameController.newMrX();
		}
	}
	private class NewDetectiveAction extends AbstractAction {
		public NewDetectiveAction() {
			putValue(NAME, "New Detective");
			putValue(SHORT_DESCRIPTION, "Add a new detective");
			putValue(MNEMONIC_KEY, KeyEvent.VK_D);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			gameController.newDetective();
		}
	}
	private class StartAction extends AbstractAction {
		public StartAction() {
			putValue(NAME, "Start");
			putValue(SHORT_DESCRIPTION, "Start the game");
			putValue(MNEMONIC_KEY, KeyEvent.VK_S);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				gameController.start();
			} catch (Exception e2) {
				showErrorMessage(e2);
			}
		}
	}
	private class AbortAction extends AbstractAction {
		public AbortAction() {
			putValue(NAME, "Abort");
			putValue(SHORT_DESCRIPTION, "Abort the game");
			putValue(MNEMONIC_KEY, KeyEvent.VK_A);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			gameController.abort();
		}
	}
	private class MoveAction extends AbstractAction {
		public MoveAction() {
			putValue(NAME, "Move...");
			putValue(SHORT_DESCRIPTION, "Make a move");
			putValue(MNEMONIC_KEY, KeyEvent.VK_M);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Move move = null;
			if (gameController.getStatus() == GameStatus.IN_GAME) {
				move = MoveProducer.createRandomSingleMove(gameState, gameGraph);
			}
			move(move);
		}
	}
	private class GameStatusWinAction extends AbstractAction {
		public GameStatusWinAction() {
			putValue(NAME, "Game Status and Win...");
			putValue(SHORT_DESCRIPTION, "Show the game status and win");
			putValue(MNEMONIC_KEY, KeyEvent.VK_I); // i as information
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			showGameStatusAndWin(gameController.getStatus(), gameController.getWin());
		}
	}
	private class RemoveDetectiveAction extends AbstractAction {
		public RemoveDetectiveAction() {
			putValue(NAME, "Remove Detective...");
			putValue(SHORT_DESCRIPTION, "Remove a detective");
			putValue(MNEMONIC_KEY, KeyEvent.VK_R);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String s = "0";
			while ((s = JOptionPane.showInputDialog(Board.this, "Enter the index of a detective", s)) != null) {
				DetectivePlayer d = null;
				try {
					d = game.getDetectives().get(Integer.parseInt(s));
				} catch (Exception e2) {
					if (JOptionPane.showConfirmDialog(Board.this, "Invalid index: " + e2.getMessage(),
							e2.getClass().getSimpleName(), JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.ERROR_MESSAGE) != JOptionPane.OK_OPTION) {
						break;
					}
					continue;
				}
				try {
					gameController.removeDetective(d);
				} catch (Exception e2) {
					showErrorMessage(e2);
				}
				// Erfolgreich oder nicht am Ende -> Raus aus Schleife
				break;
			}
		}
	}
	private class ShiftDetectiveUpAction extends AbstractAction {
		public ShiftDetectiveUpAction() {
			putValue(NAME, "Shift Detective Up...");
			putValue(SHORT_DESCRIPTION, "Shift up a detective in the list");
			putValue(MNEMONIC_KEY, KeyEvent.VK_U);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String s = "0";
			while ((s = JOptionPane.showInputDialog(Board.this, "Enter the index of a detective", s)) != null) {
				DetectivePlayer d = null;
				try {
					d = game.getDetectives().get(Integer.parseInt(s));
				} catch (Exception e2) {
					if (JOptionPane.showConfirmDialog(Board.this, "Invalid index: " + e2.getMessage(),
							e2.getClass().getSimpleName(), JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.ERROR_MESSAGE) != JOptionPane.OK_OPTION) {
						break;
					}
					continue;
				}
				try {
					gameController.shiftUpDetective(d);
				} catch (Exception e2) {
					showErrorMessage(e2);
				}
				// Erfolgreich oder nicht am Ende -> Raus aus Schleife
				break;
			}
		}
	}
	private class ShiftDetectiveDownAction extends AbstractAction {
		public ShiftDetectiveDownAction() {
			putValue(NAME, "Shift Detective Down...");
			putValue(SHORT_DESCRIPTION, "Shift down a detective in the list");
			putValue(MNEMONIC_KEY, KeyEvent.VK_D);
			putValue(DISPLAYED_MNEMONIC_INDEX_KEY, 16);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String s = "0";
			while ((s = JOptionPane.showInputDialog(Board.this, "Enter the index of a detective", s)) != null) {
				DetectivePlayer d = null;
				try {
					d = game.getDetectives().get(Integer.parseInt(s));
				} catch (Exception e2) {
					if (JOptionPane.showConfirmDialog(Board.this, "Invalid index: " + e2.getMessage(),
							e2.getClass().getSimpleName(), JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.ERROR_MESSAGE) != JOptionPane.OK_OPTION) {
						break;
					}
					continue;
				}
				try {
					gameController.shiftDownDetective(d);
				} catch (Exception e2) {
					showErrorMessage(e2);
				}
				// Erfolgreich oder nicht am Ende -> Raus aus Schleife
				break;
			}
		}
	}
	private class NewGameWithPlayersAction extends AbstractAction {
		public NewGameWithPlayersAction() {
			putValue(NAME, "New Game with Players");
			putValue(SHORT_DESCRIPTION, "Create a new game with new players");
			putValue(MNEMONIC_KEY, KeyEvent.VK_W);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			logger.info("new game with players");
			gameController.clearPlayers();
			gameController.newMrX();
			for (int i = 0; i < 4; i++)
				gameController.newDetective();
			
			gameController.newGame();
		}
	}
	private class UndoAction extends AbstractAction {
		public UndoAction() {
			putValue(NAME, "Undo");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_U);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			undoManager.undo();
			setEnabled(undoManager.canUndo()); // TODO das kanns doch nicht sein: ueberall, wo UndoManger veraendert werden koennte, muesste sowas stehn! wir brauchen nen listener!!
			setEnabled(undoManager.canRedo());
		}
	}
	private class RedoAction extends AbstractAction {
		public RedoAction() {
			putValue(NAME, "Redo");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_R);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			undoManager.redo();
			setEnabled(undoManager.canUndo()); // TODO das kanns doch nicht sein: ueberall, wo UndoManger veraendert werden koennte, muesste sowas stehn! wir brauchen nen listener!!
		    setEnabled(undoManager.canRedo());
		}
	}
	private class SuggestMoveAction extends AbstractAction {
		public SuggestMoveAction() {
			putValue(NAME, "Suggest move");
			putValue(SHORT_DESCRIPTION, "Some short description");
			putValue(MNEMONIC_KEY, KeyEvent.VK_S);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// Suggest an AI move!
			if (gameController.getStatus() == GameStatus.IN_GAME) {
				Move move = MoveProducer.createRandomSingleMove(gameState, gameGraph);
				// ... not yet
				movePreparer.nextMove(move);
			}
		}
	}
	private class MoveNowAction extends AbstractAction {
		public MoveNowAction() {
			putValue(NAME, "Move!"); // or "Move now"
			putValue(SHORT_DESCRIPTION, "Move now");
			putValue(MNEMONIC_KEY, KeyEvent.VK_M);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			move(movePreparer.getMove(gameState.getCurrentPlayer()));
		}
	}
	private class MoveDetectivesNowAction extends AbstractAction {
		public MoveDetectivesNowAction() {
			putValue(NAME, "Move Detectives!"); // alternative to simple "Move now"
			putValue(SHORT_DESCRIPTION, "Move all Detectives now");
			putValue(MNEMONIC_KEY, KeyEvent.VK_D);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Player currentPlayer = gameState.getCurrentPlayer();
			if (currentPlayer instanceof DetectivePlayer) {
				Move currentMove = movePreparer.getMove(currentPlayer);
				
				// Detectives zählen, die schon gezogen sind, oder einen Zug vorbereitet haben.
				int nDetectivesReady = 0;
				for (DetectivePlayer d : gameState.getDetectives()) {
					// TODO schon ausgefuehrte moves einbeziehen! die werden momentan ignoriert! dann meldung unten ausbessern!
					Move m = movePreparer.getMove(d);
					if (m != null) {
						nDetectivesReady++;
					}
				}
				logger.debug("result of prepared detective moves counting: " + nDetectivesReady);
				
				if (currentMove == null) {
					JOptionPane.showMessageDialog(Board.this, "The current player has not prepared it's move yet."); // TODO warnung?, dass currentPlayer noch keinen Zug vorbereitet hat
				} else if (nDetectivesReady == gameState.getDetectives().size() // Alle Detectives haben gezogen oder Zug vorbereitet
						|| JOptionPane.showConfirmDialog(Board.this,
								"It seems that not all detectives have prepared " +
								"a move yet.\nBegin moving as far as possible?",
								"Move Detectives", JOptionPane.YES_NO_OPTION)
								== JOptionPane.YES_OPTION) {

					Player p;
					while (gameController.getStatus() == GameStatus.IN_GAME
							&& (p = gameState.getCurrentPlayer()) != gameState.getMrX()) {
						
						Move m = movePreparer.getMove(p);
						if (m == null || !move(m)) {
							break;
						}
					}
				}
			}
		}
	}
	private class QuickPlayAction extends AbstractAction {
		public QuickPlayAction() {
			putValue(NAME, "Quick Play");
			putValue(SHORT_DESCRIPTION, "Toogle Quick Play mode");
			setSelected(this, true);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			ticketSelectionDialogMrX.setQuickPlay(isSelected(quickPlayAction));
			ticketSelectionDialogDetectives.setQuickPlay(isSelected(quickPlayAction));
		}
	}
	private class FitBoardAction extends AbstractAction {
		public FitBoardAction() {
			putValue(NAME, "Fit Board");
			putValue(SHORT_DESCRIPTION, "Fit the board to the window");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
			putValue(MNEMONIC_KEY, KeyEvent.VK_F);
			setSelected(this, true); // default behavior
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			setSelected(this, true);
			// pref size auf 0 setzen, damit is es immer angepasst an Viewport.
			boardPanelContainer.setPreferredSize(new Dimension());	// vorher: getImageScrollPane().getViewport().getSize());
			// Wenn man's aber an Viewport anpassen wuerde, waere es fix,
			// je nach dem, wie gross der Viewport war zu dem Zeitpunkt.
			
			boardPanelScrollPane.revalidate();
			boardPanelScrollPane.repaint();
		}
	}
	private class ZoomInAction extends AbstractAction {
		public ZoomInAction() {
			putValue(NAME, "Zoom In");
			putValue(SHORT_DESCRIPTION, "Zoom into the board");
			putValue(MNEMONIC_KEY, KeyEvent.VK_I);
			
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke((Character) '+', KeyEvent.CTRL_DOWN_MASK)); // dont work
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl typed +")); // dont work
			
			// KeyStroke.getKeyStroke('+', KeyEvent.CTRL_DOWN_MASK)
			// wird aufgefasst als getKeyStroke(int, int) -- wtf?? java castet einen char lieber automatisch als int, anstatt auto boxing zu Character.
			//  http://stackoverflow.com/questions/7931862/java-int-and-char
			// was werd ich wohl eher mit dem character '-' meinen? den character oder eine Zahl...
			
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, KeyEvent.CTRL_DOWN_MASK)); // (numpad) would work
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK)); // (non-numpad) would work
			// dabei will ich doch, dass es einfach funktioniert, wenn ein + getippt wird. dafuer ist doch auch getKeyStroke("ctrl typed ...") da, oder?
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			setSelected(fitBoardAction, false);
			zoomFactor = (double) boardPanel.getWidth() / originalImageSize.width;
			logger.debug(String.format("zoomFactor old: %f, new: %f", zoomFactor, zoomFactor * ZOMMING_FACTOR));
			zoomFactor *= ZOMMING_FACTOR;
			updateBoardPanelZoom();
		}
	}
	private class ZoomOutAction extends AbstractAction {
		public ZoomOutAction() {
			putValue(NAME, "Zoom Out");
			putValue(SHORT_DESCRIPTION, "Zoom out of the board");
			putValue(MNEMONIC_KEY, KeyEvent.VK_O);
			putValue(DISPLAYED_MNEMONIC_INDEX_KEY, 5);
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke((Character) '-', KeyEvent.CTRL_DOWN_MASK)); // dont work
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl typed -")); // dont work
			
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, KeyEvent.CTRL_DOWN_MASK)); // (numpad) would work
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK)); // (non-numpad) would work
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			setSelected(fitBoardAction, false);
			zoomFactor = (double) boardPanel.getWidth() / originalImageSize.width;
			logger.debug(String.format("zoomFactor old: %f, new: %f", zoomFactor, zoomFactor * ZOMMING_FACTOR));
			zoomFactor /= ZOMMING_FACTOR;
			updateBoardPanelZoom();
		}
	}
	private class ZoomNormalAction extends AbstractAction {
		public ZoomNormalAction() {
			putValue(NAME, "Zoom Normal");
			putValue(SHORT_DESCRIPTION, "Zoom normal");
			putValue(MNEMONIC_KEY, KeyEvent.VK_N);
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke((Character) '-', KeyEvent.CTRL_DOWN_MASK)); // dont work
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl typed -")); // dont work
			
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, KeyEvent.CTRL_DOWN_MASK)); // (numpad) would work
//			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK)); // (non-numpad) would work
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			setSelected(fitBoardAction, false);
			zoomFactor = normalZoomFactor;
			logger.debug(String.format("zoomFactor: %f", zoomFactor));
			updateBoardPanelZoom();
		}
	}
	/** Selects the current player to prepare a move for */
	private class SelectCurrentPlayerAction extends AbstractAction {
		public SelectCurrentPlayerAction() {
			putValue(NAME, "Select Current Player");
			putValue(SHORT_DESCRIPTION, "Select the current player to prepare a move for");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			movePreparer.selectPlayer(gameState.getCurrentPlayer());
		}
	}
	private class JointMovingAction extends AbstractAction {
		public JointMovingAction() {
			putValue(NAME, "Joint Moving");
			putValue(SHORT_DESCRIPTION, "Joint moving for detectives");
			setSelected(this, true);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			movePreparer.setFixedTurnOrder(!isSelected(this));
		}
	}
	private class MrXAlwaysVisibleAction extends AbstractAction {
		public MrXAlwaysVisibleAction() {
			putValue(NAME, "MrX Always Visible");
			putValue(SHORT_DESCRIPTION, "Keep MrX always visible");
			putValue(MNEMONIC_KEY, KeyEvent.VK_X); // oder M, A oder V ...
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
			setSelected(this, false);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			boardPanel.setMrXAlwaysVisible(isSelected(this));
		}
	}
	private class AboutAction extends AbstractAction {
		public AboutAction() {
			putValue(NAME, "About");
			putValue(SHORT_DESCRIPTION, "About ScotlYard");
			putValue(MNEMONIC_KEY, KeyEvent.VK_A);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(Board.this,
					"<html>" +
					"<p>ScotlYard &ndash; A software implementation of the Scotland Yard board game</p>" +
							
					"<p>Copyright (C) 2012  Jakob Schöttl</p>" +
					
					"<p>Published under the GNU GPLv3 License.</p>" +
					 
					"<p>You should have received a copy of the GNU General Public License<br/>" +
					"along with this program.  If not, see &lt;http://www.gnu.org/licenses/&gt;.</p>" +
					"</html>",
					"About", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
