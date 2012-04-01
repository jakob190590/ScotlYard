package kj.scotlyard.game.control;

import javax.swing.undo.UndoManager;

import kj.scotlyard.game.ai.detective.DetectiveAi;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.DefaultGameState;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.Rules;

public class TheGameController extends TheAbstractGameController {

	private final UndoManager undoManager = new UndoManager();
	
	private final TheNotInGameController notInGame;
	
	private final TheInGameController inGame;
	
	private GameController state;
	
	protected TheGameController(Game game, GameGraph gameGraph, Rules rules) {
		super(game, gameGraph, rules);
		notInGame = new TheNotInGameController(game, gameGraph, rules, undoManager);
		inGame = new TheInGameController(game, gameGraph, rules, undoManager);
		state = notInGame;
	}
	
	/** Should be called when GameStatus or GameWin have changed. */
	private void stateChanged() {
		setChanged();
		// soll ich GameStatus oder GameWin als param uebergeben?
		// weiss nicht -- dann lieber gar keinen param (null).
		notifyObservers();
	}

	@Override
	public void equipGameStateRequester(GameStateRequester requester) {
		if (requester instanceof DetectiveAi) {
			requester.setGameState(getRules().getGameStateAccessPolicy().createGameStateForDetectives(getGame()));
		} else {
			// Alle anderen requester (View/Controller, MrXAi) bekommen DefaultGameState
			requester.setGameState(new DefaultGameState(getGame()));
		}
	}

	@Override
	public GameStatus getStatus() {
		return state.getStatus();
	}

	@Override
	public UndoManager getUndoManager() {
		return undoManager;
	}

	@Override
	public void newGame() {
		
		state.newGame();
		
		GameWin oldWin = getWin();
		setWin(GameWin.NO);
				
		if (oldWin != GameWin.NO) {
			stateChanged();
		}
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
		setWin(state.getWin());
		
		if (getWin() == GameWin.NO) {
			state = inGame;
		}
		
		// hier aendert sich der zustand in jedem fall
		stateChanged();
	}

	@Override
	public void abort() {
		state.abort();
		state = notInGame;
		
		stateChanged();
	}

	@Override
	public void move(Move move) {
		state.move(move);
		
		GameWin win = state.getWin();
		setWin(win);
		if (win != GameWin.NO) {
			state = notInGame;
			stateChanged();
		}
	}
}
