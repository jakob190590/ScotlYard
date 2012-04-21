package kj.scotlyard.game.control.impl;

import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.MovePolicy;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.rules.Turn;
import kj.scotlyard.game.util.GameStateExtension;

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
		Turn turn = rules.getTurnPolicy().getNextTurn(game, gameGraph);		
		game.setCurrentPlayer(turn.getPlayer());
		game.setCurrentRoundNumber(turn.getRoundNumber());
		
		// GameWin ermitteln
		GameWin win = rules.getGameWinPolicy().isGameWon(game, gameGraph);
		getController().setState(this, (win == GameWin.NO) ? GameStatus.IN_GAME : GameStatus.NOT_IN_GAME, win);
		
		UndoableEdit moveEdit = new MoveEdit();
		moveEdit.addEdit(itemPassEdit);
		undoManager.addEdit(moveEdit);
	}

}
