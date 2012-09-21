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

import java.util.Map;
import java.util.Vector;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JButton;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class MovePreparationBar extends JPanel {
	
	@SuppressWarnings("unused")
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
		}
	};
	
	private JFormattedTextField ftfStationNumber;
	private JComboBox<Player> cbPlayer;
	
	private final Action submitStationNumberAction = new SubmitStationNumberAction();
	private final Action resetAction = new ResetAction();
	
	
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
		// TODO cbMovePrepPlayer.setRenderer(aRenderer); // Implement ListCellRenderer: http://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html#renderer
		cbPlayer.setPreferredSize(new Dimension(250, 20));
		gs.addPlayerListener(playerListener);
		add(cbPlayer);
		
		ftfStationNumber = new JFormattedTextField();
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
			putValue(SHORT_DESCRIPTION, "Submit station number");
		}
		public void actionPerformed(ActionEvent e) {
			mPrep.nextStation(nsm.get(Integer.parseInt(ftfStationNumber.getText())), 
					(Player) cbPlayer.getSelectedItem());
		}
	}
	private class ResetAction extends AbstractAction {
		public ResetAction() {
			putValue(NAME, "Reset");
			putValue(SHORT_DESCRIPTION, "Reset move preparation");
		}
		public void actionPerformed(ActionEvent e) {
			mPrep.reset((Player) cbPlayer.getSelectedItem());
		}
	}

}
