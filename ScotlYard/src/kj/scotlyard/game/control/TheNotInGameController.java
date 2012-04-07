package kj.scotlyard.game.control;

import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoManager;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.TheMoveProducer;
import kj.scotlyard.game.rules.GameInitPolicy;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.rules.TurnPolicy;

public class TheNotInGameController extends TheAbstractGameController {
	
	private class NewDetectiveEdit extends AbstractUndoableEdit {
		
	}
	
	

	private final UndoManager undoManager;
	
	protected TheNotInGameController(Game game, GameGraph gameGraph, Rules rules, UndoManager undoManager) {
		super(game, gameGraph, rules);
		this.undoManager = undoManager;
	}

	@Override
	public void equipGameStateRequester(GameStateRequester requester) { }

	@Override
	public GameStatus getStatus() {
		return GameStatus.NOT_IN_GAME;
	}

	@Override
	public UndoManager getUndoManager() {
		return null;
	}

	@Override
	public void newGame() {
		getGame().getMoves().clear();
		// Das reicht schon aus.
		// Andere Werte werden bei Initialisierung (start) ueberschrieben.
	}

	@Override
	public void clearPlayers() {
		getGame().setMrX(null);
		getGame().getDetectives().clear();
	}

	@Override
	public void newMrX() {
		getGame().setMrX(new MrXPlayer());
	}

	@Override
	public void newDetective() {
		getGame().getDetectives().add(new DetectivePlayer());
	}

	@Override
	public void removeDetective(DetectivePlayer detective) {
		getGame().getDetectives().remove(detective);
	}

	@Override
	public void shiftUpDetective(DetectivePlayer detective) {		
		List<DetectivePlayer> ds = getGame().getDetectives();
		int i = ds.indexOf(detective);
		if (i > 0) {
			ds.remove(detective);
			ds.add(i - 1, detective);
		}		
	}

	@Override
	public void shiftDownDetective(DetectivePlayer detective) {
		List<DetectivePlayer> ds = getGame().getDetectives();
		int i = ds.indexOf(detective);
		if (i < ds.size()) {
			ds.remove(detective);
			ds.add(i + 1, detective);
		}
	}

	@Override
	public void start() {
		Game game = getGame();
		GameGraph graph = getGameGraph();
		TheMoveProducer moveProducer = TheMoveProducer.createInstance();
				
		if (game.getMoves().size() > 0) {
			throw new IllegalStateException("Cannot start game, while Move list is not cleared. Call newGame and try again.");
		}
		
		// Valid GameState -> proceed with initialization
		GameInitPolicy initPolicy = getRules().getGameInitPolicy();
		TurnPolicy turnPolicy = getRules().getTurnPolicy();
		
		game.setCurrentRoundNumber(GameState.INITIAL_ROUND_NUMBER);
		while (turnPolicy.getNextRoundNumber(game) == GameState.INITIAL_ROUND_NUMBER) {
			Player player = turnPolicy.getNextPlayer(game);
			game.setCurrentPlayer(player);
			game.setItems(player, initPolicy.createItemSet(game, player));
			
			Station station = initPolicy.suggestInitialStation(game, graph, player);
			Move initMove = moveProducer.createInitialMove(player, station);
			game.getMoves().add(initMove);
		}
		
		setWin(getRules().getGameWinPolicy().isGameWon(game, graph));
		
	}

	@Override
	public void abort() {
		throw new IllegalStateException("We are currently NOT_IN_GAME.");
	}

	@Override
	public void move(Move move) {
		throw new IllegalStateException("We are currently NOT_IN_GAME.");
	}

}
