package kj.scotlyard.board;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
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
import kj.scotlyard.game.model.PlayerListener;
import kj.scotlyard.game.model.TurnListener;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class MovePreparationBar extends JPanel {
	
	private static final Logger logger = Logger.getLogger(MovePreparationBar.class);
	
	private GameState gs;
	private MovePreparer mPrep;
	private Map<Integer, StationVertex> nsm; // Number Station Map
	
	private Vector<Player> players;
	private final PlayerListener playerListener = new PlayerListenerAdapter() {
		@Override
		public void playerListChanged(GameState gameState) {
			players.clear();
			players.addAll(gs.getPlayers());
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
			if (newPlayer instanceof MrXPlayer) {
				cbPlayer.setEnabled(false);
			} else {
				cbPlayer.setEnabled(true);
			}
		}
	};
//	private final MoveListener moveListener = new MoveListener() {
//		@Override
//		public void movesCleard(GameState gameState) {
//		}
//		@Override
//		public void moveUndone(GameState gameState, Move move) {
//		}
//		@Override
//		public void moveDone(GameState gameState, Move move) {
//		}
//	};
	
	private final Observer movePreparerObserver = new Observer() {
		@Override
		public void update(Observable o, Object arg) {
			MovePreparationEvent mpe;
			if (arg instanceof MovePreparationEvent && (mpe = (MovePreparationEvent) arg)
					.getId() == MovePreparationEvent.SELECT_PLAYER) {
				setSelectedPlayer(mpe.getPlayer());
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
	public MovePreparationBar(GameState gs, MovePreparer mPrep,
			Map<Integer, StationVertex> nsm) {
		
		this.gs = gs;
		this.mPrep = mPrep;
		this.nsm = nsm;
		
		mPrep.addObserver(movePreparerObserver);
		
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		players = new Vector<>(gs.getPlayers());
		cbPlayer = new JComboBox<>(players);
		cbPlayer.setAction(selectPlayerAction);
		// TODO cbMovePrepPlayer.setRenderer(aRenderer); // Implement ListCellRenderer: http://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html#renderer
		cbPlayer.setRenderer(new PlayerComboBoxRenderer());
		cbPlayer.setPreferredSize(new Dimension(330, 20));
		gs.addPlayerListener(playerListener);
		gs.addTurnListener(turnListener);
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
	
	private class SubmitStationNumberAction extends AbstractAction {
		public SubmitStationNumberAction() {
			putValue(NAME, "OK");
			putValue(SHORT_DESCRIPTION, "Submit the station number");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			mPrep.nextStation(nsm.get(Integer.parseInt(ftfStationNumber.getText()))); // TODO vllt spaeter ftfStationNumber.getValue()
		}
	}
	private class ResetAction extends AbstractAction {
		public ResetAction() {
			putValue(NAME, "Reset");
			putValue(SHORT_DESCRIPTION, "Reset move preparation");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			mPrep.reset(getSelectedPlayer());
		}
	}
	private class SelectPlayerAction extends AbstractAction {
		public SelectPlayerAction() {
			putValue(NAME, "Select Player");
			putValue(SHORT_DESCRIPTION, "Select the player to prepare a move");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			mPrep.selectPlayer((Player) cbPlayer.getSelectedItem());
		}
	}
	
	public Player getSelectedPlayer() {
		return (Player) cbPlayer.getSelectedItem();
	}
	
	public void setSelectedPlayer(Player player) {
		cbPlayer.setSelectedItem(player);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (Component c : getComponents()) {
			c.setEnabled(enabled);
		}
	}
}
