package kj.scotlyard.board;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;

import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.PlayerListener;
import kj.scotlyard.game.model.TurnListener;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JButton;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class MovePreparationBar extends JPanel {
	
	private static Logger logger = Logger.getLogger(MovePreparationBar.class);
	
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
	private final TurnListener turnListener = new TurnListener() {
		@Override
		public void currentRoundChanged(GameState gameState, int oldRoundNumber,
				int newRoundNumber) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void currentPlayerChanged(GameState gameState, Player oldPlayer,
				Player newPlayer) {
			// Straight Forward
			logger.debug("current player changed: " + newPlayer);
			if (oldPlayer == getSelectedPlayer()) {
				setSelectedPlayer(newPlayer);
			}
		}
	};
	
	private final Observer movePreparerObserver = new Observer() {
		@Override
		public void update(Observable o, Object arg) {
			if (o == mPrep) {
				if (arg instanceof Player) {
					setSelectedPlayer((Player) arg);
				}
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
		
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		players = new Vector<>(gs.getPlayers());
		cbPlayer = new JComboBox<>(players);
		cbPlayer.setAction(selectPlayerAction);
		// TODO cbMovePrepPlayer.setRenderer(aRenderer); // Implement ListCellRenderer: http://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html#renderer
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
		public void actionPerformed(ActionEvent e) {
			mPrep.nextStation(nsm.get(Integer.parseInt(ftfStationNumber.getText())), // TODO vllt spaeter ftfStationNumber.getValue()
					getSelectedPlayer());
		}
	}
	private class ResetAction extends AbstractAction {
		public ResetAction() {
			putValue(NAME, "Reset");
			putValue(SHORT_DESCRIPTION, "Reset move preparation");
		}
		public void actionPerformed(ActionEvent e) {
			mPrep.reset(getSelectedPlayer());
		}
	}
	private class SelectPlayerAction extends AbstractAction {
		public SelectPlayerAction() {
			putValue(NAME, "Select Player");
			putValue(SHORT_DESCRIPTION, "Select the player to prepare a move");
		}
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
	
	public Observer getMovePreparerObserver() {
		return movePreparerObserver;
	}

}
