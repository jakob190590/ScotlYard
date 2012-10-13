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

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.TurnListener;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class MovePreparationBar extends JPanel {
	
	private static final Logger logger = Logger.getLogger(MovePreparationBar.class);

	/** For stationNumberCardLayout: page with normal input */
	private static final String NORMAL_INPUT = "normal input";
	/** For stationNumberCardLayout: page with unix style password input */
	private static final String UNIX_STYLE_PASSWORD_INPUT = "unix style password input";

	/** Refers to kind of station number input */
	private boolean hiddenInput;
	
	private GameState gameState;
	private MovePreparer movePreparer;
	private Map<Integer, StationVertex> numberStationMap; // Number Station Map
	
	// List for ComboBox Model
	private Vector<Player> players = new Vector<>();
	private final PlayerListenerAdapter playerListener = new PlayerListenerAdapter() {
		@Override
		public void playerListChanged(GameState gameState) {
			assert gameState == MovePreparationBar.this.gameState;
			players.clear();
			players.addAll(gameState.getPlayers());
			cbPlayer.updateUI();
			logger.debug("player list changed; player in move prep updated");
		}
	};
	private final TurnListener turnListener = new TurnListener() {
		@Override
		public void currentRoundChanged(GameState gameState, int oldRoundNumber,
				int newRoundNumber) { }
		@Override
		public void currentPlayerChanged(GameState gameState, Player oldPlayer,
				Player newPlayer) {
			assert gameState == MovePreparationBar.this.gameState;
			if (newPlayer instanceof MrXPlayer) {
				cbPlayer.setEnabled(false);
			} else {
				cbPlayer.setEnabled(true);
			}
		}
	};
	
	private final Observer movePreparerObserver = new Observer() {
		@Override
		public void update(Observable o, Object arg) {
			assert o == movePreparer;
			MovePreparationEvent mpe;
			if (arg instanceof MovePreparationEvent && (mpe = (MovePreparationEvent) arg)
					.getId() == MovePreparationEvent.SELECT_PLAYER) {
				cbPlayer.setSelectedItem(mpe.getPlayer());
			}
		}
	};
	
	/** To switch between two kinds of station number input */
	private CardLayout stationNumberCardLayout;
	private JPanel panelStationNumber;
	private JFormattedTextField ftfStationNumber;
	private UnixPasswordField pwfStationNumber;
	
	private JComboBox<Player> cbPlayer;
	private final PlayerComboBoxRenderer playerComboBoxRenderer = new PlayerComboBoxRenderer();
	
	
	private final Action submitStationNumberAction = new SubmitStationNumberAction();
	private final Action resetAction = new ResetAction();
	private final Action selectPlayerAction = new SelectPlayerAction();
	
	/**
	 * Create the panel.
	 */
	public MovePreparationBar() {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		cbPlayer = new JComboBox<>(players);
		cbPlayer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					MovePreparationBar.this.movePreparer.selectPlayer(
							MovePreparationBar.this.gameState.getCurrentPlayer());
				}
			}
		});
		cbPlayer.setAction(selectPlayerAction);
		cbPlayer.setRenderer(playerComboBoxRenderer);
		add(cbPlayer);
		
		panelStationNumber = new JPanel();
		add(panelStationNumber);
		stationNumberCardLayout = new CardLayout(0, 0);
		panelStationNumber.setLayout(stationNumberCardLayout);
		
		ftfStationNumber = new JFormattedTextField(NumberFormat.getInstance());
		panelStationNumber.add(ftfStationNumber, NORMAL_INPUT);
		ftfStationNumber.setAction(submitStationNumberAction);
		
		pwfStationNumber = new UnixPasswordField();
		panelStationNumber.add(pwfStationNumber, UNIX_STYLE_PASSWORD_INPUT);
		pwfStationNumber.setAction(submitStationNumberAction);
		
		JButton btnMovePrepOk = new JButton("OK");
		btnMovePrepOk.setAction(submitStationNumberAction);
		add(btnMovePrepOk);
		
		JButton btnReset = new JButton("Reset");
		btnReset.setAction(resetAction);
		add(btnReset);
		
		setHiddenInput(false);
	}
	
	@Override // TODO probleme, wenn was einzeln enabled werden soll..
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (Component c : getComponents()) {
			c.setEnabled(enabled);
		}
	}
	
	
	// Getters/Setters
	
	public boolean isHiddenInput() {
		return hiddenInput;
	}

	public void setHiddenInput(boolean hiddenInput) {
		this.hiddenInput = hiddenInput;
		if (hiddenInput) {
			stationNumberCardLayout.show(panelStationNumber, UNIX_STYLE_PASSWORD_INPUT);
		} else {
			stationNumberCardLayout.show(panelStationNumber, NORMAL_INPUT);
		}
	}
	
	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		if (gameState != this.gameState) {
			if (this.gameState != null) {
				// Player ComboBox
				players.clear();
				playerComboBoxRenderer.setGameState(null);
				// unregister listeners/observers
				this.gameState.removePlayerListener(playerListener);
				this.gameState.removeTurnListener(turnListener);
			}
			this.gameState = gameState;
			if (gameState != null) {
				// Player ComboBox
				players.addAll(gameState.getPlayers());
				playerComboBoxRenderer.setGameState(gameState);
				// register listeners/observers
				gameState.addPlayerListener(playerListener);
				gameState.addTurnListener(turnListener);
			}
			cbPlayer.updateUI();
		}
	}

	public MovePreparer getMovePreparer() {
		return movePreparer;
	}

	public void setMovePreparer(MovePreparer movePreparer) {
		if (movePreparer != this.movePreparer) {
			if (this.movePreparer != null) {
				// unregister listeners/observers
				this.movePreparer.deleteObserver(movePreparerObserver);
			}
			this.movePreparer = movePreparer;
			if (movePreparer != null) {
				// register listeners/observers
				movePreparer.addObserver(movePreparerObserver);
			}
		}
	}

	public Map<Integer, StationVertex> getNumberStationMap() {
		return numberStationMap;
	}

	public void setNumberStationMap(Map<Integer, StationVertex> numberStationMap) {
		this.numberStationMap = numberStationMap;
	}
	
	// Actions

	private class SubmitStationNumberAction extends AbstractAction {
		public SubmitStationNumberAction() {
			putValue(NAME, "OK");
			putValue(SHORT_DESCRIPTION, "Submit the station number");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String input;
			if (hiddenInput) {
				input = new String(pwfStationNumber.getPassword());
				pwfStationNumber.clear();
			} else {
				input = ftfStationNumber.getText();
				// in diesem Fall wird das text feld spaeter geleert,
				// oder auch nicht (bei ungueltiger eingabe)
			}
			
			boolean wrongInput = false;
			try {
				int number = Integer.parseInt(input);
				StationVertex station = numberStationMap.get(number);
				if (movePreparer.nextStation(station)) { // && !hiddenInput <-- bevor ich das pruefe hab ich den text auch gesetzt oder?
					ftfStationNumber.setText("");
				}
			} catch (NumberFormatException e1) {
				logger.error("wrong user input for station number: number format exception");
				wrongInput = true;
			} catch (NullPointerException e1) {
				logger.error("wrong user input for station number: station does not exist");
				wrongInput = true;
			}
			
			if (wrongInput) {
				JOptionPane.showMessageDialog(MovePreparationBar.this,
						"The station number is not valid.", "Invalid User Input",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	private class ResetAction extends AbstractAction {
		public ResetAction() {
			putValue(NAME, "Reset");
			putValue(SHORT_DESCRIPTION, "Reset move preparation");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			movePreparer.reset(movePreparer.getSelectedPlayer());
			ftfStationNumber.setText(""); // oder setValue(null) ?
		}
	}
	private class SelectPlayerAction extends AbstractAction {
		public SelectPlayerAction() {
			putValue(NAME, "Select Player");
			putValue(SHORT_DESCRIPTION, "Select the player to prepare a move");
			putValue(TOOL_TIP_TEXT_KEY, "Select the player to prepare a move or double click to select current player");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			movePreparer.selectPlayer((Player) cbPlayer.getSelectedItem());
		}
	}
}
