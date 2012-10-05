package kj.scotlyard.board;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.PlayerListener;
import kj.scotlyard.game.model.TurnListener;

@SuppressWarnings("serial")
public class PlayerPanel extends JPanel {
	
	private GameState gameState;
	
	private DefaultListModel<Player> players = new DefaultListModel<>();
	
	private final PlayerListener playerListener = new PlayerListenerAdapter() {
		@Override
		public void playerListChanged(GameState gameState) {
			assert gameState == PlayerPanel.this.gameState;
			players.removeAllElements();
			for (Player p : gameState.getPlayers()) {
				players.addElement(p);
			}
		}
	};
	private final TurnListener turnListener = new TurnListener() {
		@Override
		public void currentRoundChanged(GameState gameState, int oldRoundNumber,
				int newRoundNumber) { }
		@Override
		public void currentPlayerChanged(GameState gameState, Player oldPlayer,
				Player newPlayer) {
			assert gameState == PlayerPanel.this.gameState;
			
		}
	};

	/**
	 * Create the panel.
	 */
	public PlayerPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		JList list = new JList(players);
		scrollPane.setViewportView(list);

	}
	
	// Getters/Setters
	
	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		if (gameState != this.gameState) {
			if (this.gameState != null) {
				players.clear();
				// unregister listeners/observers
				this.gameState.removePlayerListener(playerListener);
				this.gameState.removeTurnListener(turnListener);
			}
			this.gameState = gameState;
			if (gameState != null) {
				for (Player p : gameState.getPlayers()) {
					players.addElement(p);
				}
				// register listeners/observers
				gameState.addPlayerListener(playerListener);
				gameState.addTurnListener(turnListener);
			}
//			.updateUI();
		}
	}

}
