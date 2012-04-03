package kj.scotlyard.game.control;

import java.util.List;

import javax.swing.undo.UndoManager;

import kj.scotlyard.game.ai.detective.DetectiveAi;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.DefaultGameState;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.TheMoveProducer;
import kj.scotlyard.game.rules.GameInitPolicy;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.MovePolicy;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.rules.TurnPolicy;

public class TheGameController extends GameController {

	private final Game game;
	
	private final GameGraph gameGraph;
	
	private Rules rules;
	
	
	private final UndoManager undoManager = new UndoManager();
	
	
	private final NotInGameControllerState notInGame;
	
	private final InGameControllerState inGame;
	
	private GameController state;
	
	
	private GameWin win;
	
	public TheGameController(Game game, GameGraph gameGraph, Rules rules) {
		this.game = game;
		this.gameGraph = gameGraph;
		this.rules = rules;
		
		notInGame = new NotInGameControllerState(this);
		inGame = new InGameControllerState(this);
		
		state = notInGame;
		// status = GameStatus.NOT_IN_GAME (implizit durch zeile oberhalb)
		win = GameWin.NO;
	}
	
void setState(GameController sender, GameStatus status, GameWin win) {
		
		// only the own current state can change the state
		if (sender == state) {
			
			if (status == null || win == null) {
				throw new IllegalArgumentException("GameStatus and GameWin must not be null.");
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
			throw new RuntimeException(""); // TODO was fuer ne exception?
		}
		
	}
	
	protected Game getGame() {
		return game;
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




abstract class GameControllerState extends GameController {
	
	private TheGameController controller;

	protected GameControllerState(TheGameController controller) {
		this.controller = controller;
	}
	
	protected TheGameController getController() {
		return controller;
	}

	@Override
	public GameWin getWin() {
		return controller.getWin();
	}
	
}




class InGameControllerState extends GameControllerState {
	
	protected InGameControllerState(TheGameController controller) {
		super(controller);
	}

	private void changeTicketOwner(Game game, MovePolicy policy, Move move) {
		
		Player p1 = move.getPlayer();
		Player p2 = policy.getNextItemOwner(game, move, move.getItem());
		
		game.getItems(p1).remove(move.getItem());
		game.getItems(p2).add(move.getItem());
		
		// Rekursiv fuer alle Sub Moves (if any)
		for (Move m : move.getMoves()) {
			changeTicketOwner(game, policy, m);
		}
	}

	@Override
	public GameStatus getStatus() {
		return GameStatus.IN_GAME;
	}
	
	@Override
	public void newGame() {
		throw new IllegalStateException("For this operation we must be NOT_IN_GAME. Finish the game first or use abort.");
	}

	@Override
	public void clearPlayers() {
		throw new IllegalStateException("For this operation we must be NOT_IN_GAME. Finish the game first or use abort.");
	}

	@Override
	public void newMrX() {
		throw new IllegalStateException("For this operation we must be NOT_IN_GAME. Finish the game first or use abort.");
	}

	@Override
	public void newDetective() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDetective(DetectivePlayer detective) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shiftUpDetective(DetectivePlayer detective) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shiftDownDetective(DetectivePlayer detective) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		throw new IllegalStateException("For this operation we must be NOT_IN_GAME. Finish the game first or use abort.");
	}

	@Override
	public void abort() {
		// nothing to do
		// zustandsaenderungen uebernimmt der "state holder",
		// also TheGameController
	}

	@Override
	public void move(Move move) {
		
		Game game = getController().getGame();
		GameGraph graph = getController().getGameGraph();
		
		// Move abfertigen (fehlende Params setzen)
		
		
		MovePolicy movePolicy = getController().getRules().getMovePolicy();
		
		movePolicy.checkMove(game, graph, move);
		// TODO move.seal() hier oder beim Add im model?
		
		// Tickets richtig weitergeben
		changeTicketOwner(game, movePolicy, move);
		
		// Move eintragen
		game.getMoves().add(move);
		
		// Current sachen nach Move aktualisieren
		Player nextPlayer = getController().getRules().getTurnPolicy().getNextPlayer(game);
		int nextRoundNumber = getController().getRules().getTurnPolicy().getNextRoundNumber(game);
		
		game.setCurrentPlayer(nextPlayer);
		game.setCurrentRoundNumber(nextRoundNumber);
		
		// GameWin ermitteln
		GameWin win = getController().getRules().getGameWinPolicy().isGameWon(game, graph);
		getController().setState(this, (win == GameWin.NO) ? GameStatus.IN_GAME : GameStatus.NOT_IN_GAME, win); 		
	}

}





class NotInGameControllerState extends GameControllerState {
	
	protected NotInGameControllerState(TheGameController controller) {
		super(controller);
	}

//	@SuppressWarnings("serial")
//	private class NewDetectiveEdit extends AbstractUndoableEdit {
//		
//		private DetectivePlayer detective;
//		
//		public NewDetectiveEdit(DetectivePlayer detective) {
//			this.detective = detective;
//		}
//
//		@Override
//		public void undo() throws CannotUndoException {
//			super.undo();
//			getGame().getDetectives().remove(detective);
//		}
//
//		@Override
//		public void redo() throws CannotRedoException {
//			super.redo();
//			getGame().getDetectives().add(detective);
//		}
//		
//	}

	


	@Override
	public GameStatus getStatus() {
		return GameStatus.NOT_IN_GAME;
	}

	@Override
	public void newGame() {
		getController().getGame().getMoves().clear();
		// Das reicht schon aus.
		// Andere Werte werden bei Initialisierung (start) ueberschrieben.
	}

	@Override
	public void clearPlayers() {
		getController().getGame().setMrX(null);
		getController().getGame().getDetectives().clear();
	}

	@Override
	public void newMrX() {
		getController().getGame().setMrX(new MrXPlayer());
	}

	@Override
	public void newDetective() {
		DetectivePlayer d = new DetectivePlayer();
		getController().getGame().getDetectives().add(d);
		
	}

	@Override
	public void removeDetective(DetectivePlayer detective) {
		getController().getGame().getDetectives().remove(detective);
	}

	@Override
	public void shiftUpDetective(DetectivePlayer detective) {		
		List<DetectivePlayer> ds = getController().getGame().getDetectives();
		int i = ds.indexOf(detective);
		if (i > 0) {
			ds.remove(detective);
			ds.add(i - 1, detective);
		}		
	}

	@Override
	public void shiftDownDetective(DetectivePlayer detective) {
		List<DetectivePlayer> ds = getController().getGame().getDetectives();
		int i = ds.indexOf(detective);
		if (i < ds.size()) {
			ds.remove(detective);
			ds.add(i + 1, detective);
		}
	}

	@Override
	public void start() {
		Game game = getController().getGame();
		GameGraph graph = getController().getGameGraph();
		TheMoveProducer moveProducer = TheMoveProducer.createInstance();
				
		if (game.getMoves().size() > 0) {
			throw new IllegalStateException("Cannot start game, while Move list is not cleared. Call newGame and try again.");
		}
		
		// Valid GameState -> proceed with initialization
		GameInitPolicy initPolicy = getController().getRules().getGameInitPolicy();
		TurnPolicy turnPolicy = getController().getRules().getTurnPolicy();
		
		game.setCurrentRoundNumber(GameState.INITIAL_ROUND_NUMBER);
		while (turnPolicy.getNextRoundNumber(game) == GameState.INITIAL_ROUND_NUMBER) {
			Player player = turnPolicy.getNextPlayer(game);
			game.setCurrentPlayer(player);
			game.setItems(player, initPolicy.createItemSet(game, player));
			
			StationVertex station = initPolicy.suggestInitialStation(game, graph, player);
			Move initMove = moveProducer.createInitialMove(player, station);
			game.getMoves().add(initMove);
		}

		GameWin win = getController().getRules().getGameWinPolicy().isGameWon(game, graph);
		getController().setState(this, (win == GameWin.NO) ? GameStatus.IN_GAME : GameStatus.NOT_IN_GAME, win);
		
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
