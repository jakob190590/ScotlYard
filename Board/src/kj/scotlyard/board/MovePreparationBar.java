package kj.scotlyard.board;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
	
	private JFormattedTextField ftfStationNumber;
	private JComboBox<Player> cbPlayer;
	
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
		cbPlayer.setRenderer(new PlayerComboBoxRenderer(gameState));
		add(cbPlayer);
		
		ftfStationNumber = new JFormattedTextField();
		ftfStationNumber.setAction(submitStationNumberAction);
		add(ftfStationNumber);
		
		JButton btnMovePrepOk = new JButton("OK");
		btnMovePrepOk.setAction(submitStationNumberAction);
		add(btnMovePrepOk);
		
		JButton btnReset = new JButton("Reset");
		btnReset.setAction(resetAction);
		add(btnReset);
	}
	
	@Override // TODO probleme, wenn was einzeln enabled werden soll..
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (Component c : getComponents()) {
			c.setEnabled(enabled);
		}
	}
	
	
	// Getters/Setters
	
	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		if (gameState != this.gameState) {
			if (this.gameState != null) {
				// unregister listeners/observers
				this.gameState.removePlayerListener(playerListener);
				this.gameState.removeTurnListener(turnListener);
				players.clear();
			}
			this.gameState = gameState;
			if (gameState != null) {
				players.addAll(gameState.getPlayers());
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
			if (movePreparer.nextStation(numberStationMap.get(Integer.parseInt(ftfStationNumber.getText())))) { // TODO vllt spaeter ftfStationNumber.getValue()
				ftfStationNumber.setText(""); // oder setValue(null) ?
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
