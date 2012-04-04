package kj.scotlyard.game.control;

import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

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
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.rules.GameInitPolicy;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.MovePolicy;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.rules.TurnPolicy;
import kj.scotlyard.game.util.GameStateExtension;

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
			throw new SecurityException("Only this' own current state implementation can change the state.");
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
	
	@SuppressWarnings("serial")
	private class ItemPassEdit extends AbstractUndoableEdit {
		
		Player p1, p2;
		
		Item item;
		
		/**
		 * Zum Weitergeben eines Item von player1 an player2.
		 * @param player1 der, der das Item hergeben muss.
		 * @param player2 der, der das Item bekommt. Kann
		 * auch wieder player1 selbst sein, oder null.
		 * @param item das Item, das weitergegeben wird.
		 */
		public ItemPassEdit(Player player1, Player player2, Item item) {
			this.p1 = player1;
			this.p2 = player2;
			this.item = item;
		}
		
		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			if (p2 != null) {
				game.getItems(p2).remove(item);
			}
			game.getItems(p1).add(item);
			
		}

		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			game.getItems(p1).remove(item);
			if (p2 != null) {
				game.getItems(p2).add(item);
			}
		}
		
	}
	
	@SuppressWarnings("serial")
	private class MoveEdit extends AbstractUndoableEdit {
		
		private Move move;
		
		private GameStatus status;
		
		private GameWin win;
		// TODO da gehoert noch mehr dazu: items, current player/round
		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			status = getController().getStatus();
			win = getController().getWin();
			
			move = game.getMoves().remove(GameState.LAST_MOVE);
			
			// falls es der letzte zug war: zustand wieder auf IN_GAME setzen
			getController().setState(InGameControllerState.this, GameStatus.IN_GAME, GameWin.NO);
		}

		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			game.getMoves().add(move);
			getController().setState(InGameControllerState.this, status, win);
		}
		
	}
	
	
	private final Game game;
	
	private final GameStateExtension gameStateExtension;
	
	private final GameGraph gameGraph;
	
	private final UndoManager undoManager;
	
	protected InGameControllerState(TheGameController controller) {
		super(controller);
		game = controller.getGame();
		gameStateExtension = new GameStateExtension(game);
		gameGraph = controller.getGameGraph();
		undoManager = controller.getUndoManager();
	}
	
	private void raiseIllegalStateException() {
		throw new IllegalStateException("For this operation we must be NOT_IN_GAME. Finish the game first or use abort.");
	}

	private UndoableEdit passItems(Game game, MovePolicy policy, Move move) {
		
		UndoableEdit edit = null;
		UndoableEdit lastEdit = null;
		UndoableEdit nextEdit;
		
		Player p1 = move.getPlayer();
		List<Move> moves = gameStateExtension.flattenMove(move, false);
		
		for (Move m : moves) {
			Item item = m.getItem();
			Player p2 = policy.getNextItemOwner(game, move, item);
			if (p1 != p2) {
				
				game.getItems(p1).remove(item);
				if (p2 != null) {
					game.getItems(p2).add(item);
				}
				
				nextEdit = new ItemPassEdit(p1, p2, item);
				if (edit == null) {
					lastEdit = edit = nextEdit; 
				} else {
					lastEdit.addEdit(nextEdit);
					lastEdit = nextEdit;
				}
				
			}			
		}
		
		return edit;
	}

	@Override
	public GameStatus getStatus() {
		return GameStatus.IN_GAME;
	}
	
	@Override
	public void newGame() {
		raiseIllegalStateException();
	}

	@Override
	public void clearPlayers() {
		raiseIllegalStateException();
	}

	@Override
	public void newMrX() {
		raiseIllegalStateException();
	}

	@Override
	public void newDetective() {
		throw new IllegalStateException("Not yet supported, while IN_GAME."); // TODO add support
	}

	@Override
	public void removeDetective(DetectivePlayer detective) {
		throw new IllegalStateException("Not yet supported, while IN_GAME."); // TODO add support
	}

	@Override
	public void shiftUpDetective(DetectivePlayer detective) {
		throw new IllegalStateException("Not yet supported, while IN_GAME."); // TODO add support
	}

	@Override
	public void shiftDownDetective(DetectivePlayer detective) {
		throw new IllegalStateException("Not yet supported, while IN_GAME."); // TODO add support
	}

	@Override
	public void start() {
		raiseIllegalStateException();
	}

	@Override
	public void abort() {
		getController().setState(this, GameStatus.NOT_IN_GAME, GameWin.NO);
	}

	@Override
	public void move(Move move) {

		final Rules rules = getController().getRules();		
		final MovePolicy movePolicy = rules.getMovePolicy();
		
		movePolicy.checkMove(game, gameGraph, move);
		
		// move.seal(): das macht game.getMoves().add(..)		
		
		// Move eintragen
		game.getMoves().add(move);
		
		// Tickets richtig weitergeben
		UndoableEdit itemPassEdit = passItems(game, movePolicy, move);
		
		// Turn/Current sachen nach Move aktualisieren
		Player nextPlayer = rules.getTurnPolicy().getNextPlayer(game);
		int nextRoundNumber = rules.getTurnPolicy().getNextRoundNumber(game);
		
		game.setCurrentPlayer(nextPlayer);
		game.setCurrentRoundNumber(nextRoundNumber);
		
		// GameWin ermitteln
		GameWin win = rules.getGameWinPolicy().isGameWon(game, gameGraph);
		getController().setState(this, (win == GameWin.NO) ? GameStatus.IN_GAME : GameStatus.NOT_IN_GAME, win);
		
		UndoableEdit moveEdit = new MoveEdit();
		moveEdit.addEdit(itemPassEdit);
		undoManager.addEdit(moveEdit);
	}

}





class NotInGameControllerState extends GameControllerState {
	
	@SuppressWarnings("serial")
	private class NewMrXEdit extends AbstractUndoableEdit {
		
		private MrXPlayer oldMrX;
		
		private MrXPlayer newMrX;
		
		public NewMrXEdit(MrXPlayer oldMrX) {
			this.oldMrX = oldMrX;
		}
		
		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			newMrX = game.getMrX();
			game.setMrX(oldMrX);
		}
		
		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			game.setMrX(newMrX);
		}
		
	}
	
	@SuppressWarnings("serial")
	private class NewDetectiveEdit extends AbstractUndoableEdit {
		
		private DetectivePlayer detective;
		
		private int index;
		
		public NewDetectiveEdit(DetectivePlayer detective) {
			this.detective = detective;
		}

		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			index = game.getDetectives().indexOf(detective);
			game.getDetectives().remove(detective);
		}

		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			game.getDetectives().add(index, detective);
		}
		
	}
	
	@SuppressWarnings("serial")
	private class RemoveDetectiveEdit extends AbstractUndoableEdit {
		
		private DetectivePlayer detective;
		
		private int index;
		
		public RemoveDetectiveEdit(int index, DetectivePlayer detective) {
			this.detective = detective;
			this.index = index;
		}
		
		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			game.getDetectives().add(index, detective);
		}
		
		@Override
		public void redo() throws CannotRedoException {
			super.redo();			
			game.getDetectives().remove(detective);
		}
		
	}
	
	@SuppressWarnings("serial")
	private class ShiftUpDetectiveEdit extends AbstractUndoableEdit {
		
		private DetectivePlayer detective;
		
		private int oldIndex;
		
		public ShiftUpDetectiveEdit(int oldIndex, DetectivePlayer detective) {
			this.detective = detective;
			this.oldIndex = oldIndex;
		}
		
		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			game.getDetectives().remove(detective);
			game.getDetectives().add(oldIndex, detective);
		}
		
		@Override
		public void redo() throws CannotRedoException {
			super.redo();			
			game.getDetectives().remove(detective);
			game.getDetectives().add(oldIndex - 1, detective);
		}
		
	}
	
	@SuppressWarnings("serial")
	private class ShiftDownDetectiveEdit extends AbstractUndoableEdit {
		
		private DetectivePlayer detective;
		
		private int oldIndex;
		
		public ShiftDownDetectiveEdit(int oldIndex, DetectivePlayer detective) {
			this.detective = detective;
			this.oldIndex = oldIndex;
		}
		
		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			game.getDetectives().remove(detective);
			game.getDetectives().add(oldIndex, detective);
		}
		
		@Override
		public void redo() throws CannotRedoException {
			super.redo();			
			game.getDetectives().remove(detective);
			game.getDetectives().add(oldIndex + 1, detective);
		}
		
	}
	
	
	
	private final Game game;
	
	private final GameGraph gameGraph;
	
	private final UndoManager undoManager;
	
	protected NotInGameControllerState(TheGameController controller) {
		super(controller);
		game = controller.getGame();
		gameGraph = controller.getGameGraph();
		undoManager = controller.getUndoManager();
	}

	private void raiseIllegalStateException() {
		throw new IllegalStateException("We are currently NOT_IN_GAME.");
	}
	
	@Override
	public GameStatus getStatus() {
		return GameStatus.NOT_IN_GAME;
	}

	@Override
	public void newGame() {
		getController().getGame().getMoves().clear();
		// Das reicht schon aus.
		// Andere Werte werden bei Initialisierung (start) ueberschrieben.
		undoManager.discardAllEdits();
	}

	@Override
	public void clearPlayers() {
		getController().getGame().setMrX(null);
		getController().getGame().getDetectives().clear();
		undoManager.discardAllEdits(); // TODO oder auch Undoable machen?
	}

	@Override
	public void newMrX() {
		undoManager.addEdit(new NewMrXEdit(game.getMrX()));
		game.setMrX(new MrXPlayer());
	}

	@Override
	public void newDetective() {
		// TODO regeln beachten?
		DetectivePlayer d = new DetectivePlayer();
		game.getDetectives().add(d);
		undoManager.addEdit(new NewDetectiveEdit(d));
	}

	@Override
	public void removeDetective(DetectivePlayer detective) {
		// TODO regeln beachten?
		undoManager.addEdit(new RemoveDetectiveEdit(game.getDetectives().indexOf(detective), detective));
		game.getDetectives().remove(detective);
	}

	@Override
	public void shiftUpDetective(DetectivePlayer detective) {
		List<DetectivePlayer> ds = getController().getGame().getDetectives();
		int i = ds.indexOf(detective);
		if (i > 0) {
			ds.remove(detective);
			ds.add(i - 1, detective);
			undoManager.addEdit(new ShiftUpDetectiveEdit(i, detective));
		}		
	}

	@Override
	public void shiftDownDetective(DetectivePlayer detective) {
		List<DetectivePlayer> ds = getController().getGame().getDetectives();
		int i = ds.indexOf(detective);
		if (i < ds.size()) {
			ds.remove(detective);
			ds.add(i + 1, detective);
			undoManager.addEdit(new ShiftDownDetectiveEdit(i, detective));
		}
	}

	@Override
	public void start() {
		TheMoveProducer moveProducer = TheMoveProducer.createInstance();
				
		if (!game.getMoves().isEmpty()) {
			throw new IllegalStateException("Cannot start game, while Move list is not cleared. Call newGame and try again.");
		}
		
		// TODO rules zu detective count beachten?
		
		// Valid GameState -> proceed with initialization
		Rules rules = getController().getRules();
		GameInitPolicy initPolicy = rules.getGameInitPolicy();
		TurnPolicy turnPolicy = rules.getTurnPolicy();
		
		game.setCurrentRoundNumber(GameState.INITIAL_ROUND_NUMBER);
		while (turnPolicy.getNextRoundNumber(game) == GameState.INITIAL_ROUND_NUMBER) {
			Player player = turnPolicy.getNextPlayer(game);
			game.setCurrentPlayer(player);
			game.setItems(player, initPolicy.createItemSet(game, player));
			
			StationVertex station = initPolicy.suggestInitialStation(game, gameGraph, player);
			Move initMove = moveProducer.createInitialMove(player, station);
			game.getMoves().add(initMove);
		}
		game.setCurrentRoundNumber(turnPolicy.getNextRoundNumber(game));

		GameWin win = getController().getRules().getGameWinPolicy().isGameWon(game, gameGraph);
		getController().setState(this, (win == GameWin.NO) ? GameStatus.IN_GAME : GameStatus.NOT_IN_GAME, win);
		
	}

	@Override
	public void abort() {
		raiseIllegalStateException();
	}

	@Override
	public void move(Move move) {
		raiseIllegalStateException();
	}
	
}
