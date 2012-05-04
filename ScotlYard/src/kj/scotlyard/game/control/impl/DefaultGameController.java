package kj.scotlyard.game.control.impl;

import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;

import kj.scotlyard.game.ai.detective.DetectiveAi;
import kj.scotlyard.game.control.GameController;
import kj.scotlyard.game.control.GameStateRequester;
import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.DefaultGameState;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.Rules;

public class DefaultGameController extends GameController {
	
	@SuppressWarnings("serial")
	protected class MoveEdit extends CompoundEdit {

		private Player oldPlayer;
		
		private int oldRoundNumber;
		
		// Werden beim Undo ermittelt:
		
		private int roundNumber;
		
		private Player player;
		
		private Move move;
		
		private GameStatus status;
		
		private GameWin win;

		public MoveEdit(Player oldPlayer, int oldRoundNumber) {
			this.oldPlayer = oldPlayer;
			this.oldRoundNumber = oldRoundNumber;
		}

		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			
			player = game.getCurrentPlayer();
			roundNumber = game.getCurrentRoundNumber();
			
			status = getStatus();
			win = getWin();
			
			move = game.getMoves().remove(GameState.LAST_MOVE);
			
			game.setCurrentPlayer(oldPlayer);
			game.setCurrentRoundNumber(oldRoundNumber);
			
			// falls es der game-winning move war: zustand wieder auf IN_GAME setzen
			if (status == GameStatus.NOT_IN_GAME)
				setState(notInGame, GameStatus.IN_GAME, GameWin.NO);
		}

		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			
			game.getMoves().add(move);
			
			game.setCurrentPlayer(player);
			game.setCurrentRoundNumber(roundNumber);
			
			setState(inGame, status, win);
		}
		
	}
	
	@SuppressWarnings("serial")
	protected class AbortEdit extends AbstractUndoableEdit {
		
		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			setState(notInGame, GameStatus.IN_GAME, GameWin.NO);
		}
		
		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			setState(inGame, GameStatus.NOT_IN_GAME, GameWin.NO);
		}
		
	}


	private final Game game;
	
	private final GameGraph gameGraph;
	
	private final Set<StationVertex> initialStations;
	
	private Rules rules;
	
	
	private final UndoManager undoManager = new UndoManager();
	
	
	private final NotInGameControllerState notInGame;
	
	private final InGameControllerState inGame;
	
	private GameController state;
	
	
	private GameWin win;
	
	public DefaultGameController(Game game, GameGraph gameGraph, Set<StationVertex> initialStations, Rules rules) {
		this.game = game;
		this.gameGraph = gameGraph;
		this.initialStations = initialStations;
		this.rules = rules;
		
		notInGame = new NotInGameControllerState(this);
		inGame = new InGameControllerState(this);
		
		state = notInGame;
		// status = GameStatus.NOT_IN_GAME (implizit durch zeile oberhalb)
		win = GameWin.NO;
	}
	
	/**
	 * Aendert den aktuellen Zustand des Controllers. Dieser kann 
	 * nur vom eigenen momentanen Zustand geaendert werden.
	 * @param sender the state object requesting the change of state
	 * @param status the new status (in or not in game)
	 * @param win the new win (MrX/Detectives/No)
	 */
	void setState(GameController sender, GameStatus status, GameWin win) {
		
		// only the own current state can change the state
		if (sender == state) {
			
			if (status == null || win == null) {
				throw new NullPointerException("GameStatus and GameWin must not be null.");
			}
			
			if (status != getStatus()) {
			
				switch (status) {
				case IN_GAME:
					state = inGame;
					break;
				case NOT_IN_GAME:
					state = notInGame;
					break;
				}
				
				setChanged();
			}
			
			if (win != getWin()) {
				this.win = win;
				setChanged();
			}
			
			// nur notify, wenn wirklich was geaendert (deswegen setChanged)
			
			// soll ich GameStatus oder GameWin als param uebergeben?
			// weiss nicht -- dann lieber gar keinen param (null).
			notifyObservers();
						
		} else {
			throw new SecurityException("Only this' own current state implementation can change the state.");
		}
		
	}
	
	protected Game getGame() {
		return game;
	}
	
	protected Set<StationVertex> getInitialStations() { // TODO public or protected? wenn getgraph public is...
		return initialStations;
	}

	public void equipGameStateRequester(GameStateRequester requester) {
		if (requester instanceof DetectiveAi) {
			requester.setGameState(getRules().getGameStateAccessPolicy().createGameStateForDetectives(getGame()));
		} else {
			// Alle anderen requester (View/Controller, MrXAi) bekommen DefaultGameState
			requester.setGameState(new DefaultGameState(getGame()));
		}
	}

	public GameGraph getGameGraph() {
		return gameGraph;
	}
		
	public Rules getRules() {
		return rules;
	}
	
	public void setRules(Rules rules) {
		this.rules = rules;
	}
	
	
	@Override
	public GameStatus getStatus() {
		return state.getStatus();
	}
	
	@Override
	public GameWin getWin() {
		return win;
	}

	
	public UndoManager getUndoManager() {
		return undoManager;
	}

	
	@Override
	public void newGame() {
		state.newGame();
	}

	@Override
	public void clearPlayers() {
		state.clearPlayers();
	}

	@Override
	public void newMrX() {
		state.newMrX();
	}

	@Override
	public void newDetective() {
		state.newDetective();
	}

	@Override
	public void removeDetective(DetectivePlayer detective) {
		state.removeDetective(detective);
	}

	@Override
	public void shiftUpDetective(DetectivePlayer detective) {
		state.shiftUpDetective(detective);
	}

	@Override
	public void shiftDownDetective(DetectivePlayer detective) {
		state.shiftDownDetective(detective);
	}

	@Override
	public void start() {
		state.start();
	}

	@Override
	public void abort() {
		state.abort();
	}

	@Override
	public void move(Move move) {
		state.move(move);
	}
}
