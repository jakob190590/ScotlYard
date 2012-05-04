package kj.scotlyard.game.model;

import java.util.HashSet;
import java.util.Set;

import kj.scotlyard.game.model.item.Item;

/**
 * This class implements the listener handling. It organizes the listeners in
 * unordered <tt>Set</tt>s and provides protected listener informer, which call
 * the desired operation on all registered listeners.
 * 
 * For the <tt>GameState</tt> argument of the listener methods, the passed value
 * will be always <tt>this</tt>!
 * 
 * @author jakob190590
 * 
 */
public abstract class AbstractGameState implements GameState {

	private final Set<TurnListener> stateListeners = new HashSet<TurnListener>();

	private final Set<PlayerListener> playerListeners = new HashSet<PlayerListener>();

	private final Set<ItemListener> itemListeners = new HashSet<ItemListener>();

	private final Set<MoveListener> moveListeners = new HashSet<MoveListener>();

	// Informer, that informs the registered listeners

	private final TurnListener stateListenerInformer = new TurnListener() {

		@Override
		public void currentRoundChanged(GameState gameState,
				int oldRoundNumber, int newRoundNumber) {
			for (TurnListener l : stateListeners) {
				l.currentRoundChanged(AbstractGameState.this, oldRoundNumber,
						newRoundNumber);
			}
		}

		@Override
		public void currentPlayerChanged(GameState gameState, Player oldPlayer,
				Player newPlayer) {
			for (TurnListener l : stateListeners) {
				l.currentPlayerChanged(AbstractGameState.this, oldPlayer,
						newPlayer);
			}
		}
	};

	private final PlayerListener playerListenerInformer = new PlayerListener() {

		@Override
		public void mrXSet(GameState gameState, MrXPlayer oldMrX,
				MrXPlayer newMrX) {
			for (PlayerListener l : playerListeners) {
				l.mrXSet(AbstractGameState.this, oldMrX, newMrX);
			}
		}

		@Override
		public void detectiveRemoved(GameState gameState,
				DetectivePlayer detective, int atIndex) {
			for (PlayerListener l : playerListeners) {
				l.detectiveRemoved(AbstractGameState.this, detective, atIndex);
			}
		}

		@Override
		public void detectiveAdded(GameState gameState,
				DetectivePlayer detective, int atIndex) {
			for (PlayerListener l : playerListeners) {
				l.detectiveAdded(AbstractGameState.this, detective, atIndex);
			}
		}
	};

	private final ItemListener itemListenerInformer = new ItemListener() {

		@Override
		public void itemSetChanged(GameState gameState, Player player,
				Set<Item> oldItems, Set<Item> newItems) {
			for (ItemListener l : itemListeners) {
				l.itemSetChanged(AbstractGameState.this, player, oldItems,
						newItems);
			}
		}

		@Override
		public void itemRemoved(GameState gameState, Player player, Item item) {
			for (ItemListener l : itemListeners) {
				l.itemRemoved(AbstractGameState.this, player, item);
			}
		}

		@Override
		public void itemAdded(GameState gameState, Player player, Item item) {
			for (ItemListener l : itemListeners) {
				l.itemAdded(AbstractGameState.this, player, item);
			}
		}
	};

	private final MoveListener moveListenerInformer = new MoveListener() {

		@Override
		public void movesCleard(GameState gameState) {
			for (MoveListener l : moveListeners) {
				l.movesCleard(AbstractGameState.this);
			}
		}

		@Override
		public void moveUndone(GameState gameState, Move move) {
			for (MoveListener l : moveListeners) {
				l.moveUndone(AbstractGameState.this, move);
			}
		}

		@Override
		public void moveDone(GameState gameState, Move move) {
			for (MoveListener l : moveListeners) {
				l.moveDone(AbstractGameState.this, move);
			}
		}
	};

	// Access to listeners informer

	protected TurnListener getStateListenerInformer() {
		return stateListenerInformer;
	}

	protected PlayerListener getPlayerListenerInformer() {
		return playerListenerInformer;
	}

	protected ItemListener getItemListenerInformer() {
		return itemListenerInformer;
	}

	protected MoveListener getMoveListenerInformer() {
		return moveListenerInformer;
	}

	// TODO sind folgende getter notwendig?
	protected Set<TurnListener> getStateListeners() {
		return stateListeners;
	}

	protected Set<PlayerListener> getPlayerListeners() {
		return playerListeners;
	}

	protected Set<ItemListener> getItemListeners() {
		return itemListeners;
	}

	protected Set<MoveListener> getMoveListeners() {
		return moveListeners;
	}

	// Listener registration

	@Override
	public void addStateListener(TurnListener listener) {
		stateListeners.add(listener);
	}

	@Override
	public void removeStateListener(TurnListener listener) {
		stateListeners.remove(listener);
	}

	@Override
	public void addPlayerListener(PlayerListener listener) {
		playerListeners.add(listener);
	}

	@Override
	public void removePlayerListener(PlayerListener listener) {
		playerListeners.remove(listener);
	}

	@Override
	public void addItemListener(ItemListener listener) {
		itemListeners.add(listener);
	}

	@Override
	public void removeItemListener(ItemListener listener) {
		itemListeners.remove(listener);
	}

	@Override
	public void addMoveListener(MoveListener listener) {
		moveListeners.add(listener);
	}

	@Override
	public void removeMoveListener(MoveListener listener) {
		moveListeners.remove(listener);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof GameState) {
			GameState g = (GameState) obj;	
			
			if (getMrX() != g.getMrX()
					&& (getMrX() == null
					|| !getMrX().equals(g.getMrX())))
				return false;
					
			if (!getDetectives().equals(g.getDetectives()))
				return false;
			
			if (!getMoves().equals(g.getMoves()))
				return false;
					
			if (getCurrentPlayer() != g.getCurrentPlayer()
					&& (getCurrentPlayer() == null 
					|| !getCurrentPlayer().equals(g.getCurrentPlayer())))
				return false;
					
			if (getCurrentRoundNumber() != g.getCurrentRoundNumber())
				return false;
				
			for (Player p : getPlayers()) {
				Set<Item> i1 = getItems(p);
				Set<Item> i2 = g.getItems(p);					
				if (i1 != i2 && (i1 == null || !i1.equals(i2)))
					return false;
			}
			
			// Alle Tests bestanden
			return true;
		}
		return false;
	}

}
