package kj.scotlyard.game.control.impl;

import java.util.LinkedList;
import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;

import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
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
	
	
	
	private final Game game;
	
	private final GameStateExtension gameStateExtension;
	
	private final GameGraph gameGraph;
	
	private final UndoManager undoManager;
	
	protected InGameControllerState(DefaultGameController controller) {
		super(controller);
		game = controller.getGame();
		gameStateExtension = new GameStateExtension(game);
		gameGraph = controller.getGameGraph();
		undoManager = controller.getUndoManager();
	}
	
	private void raiseIllegalStateException() {
		throw new IllegalStateException("For this operation we must be NOT_IN_GAME. Finish the game first or use abort.");
	}

	private List<ItemPassEdit> passItems(Game game, MovePolicy policy, Move move) {
		
		List<ItemPassEdit> edits = new LinkedList<>();
		
		Player p1 = move.getPlayer();
		List<Move> moves = gameStateExtension.flattenMove(move, true);
		
		for (Move m : moves) {
			Item item = m.getItem();
			Player p2 = policy.getNextItemOwner(game, move, item);
			if (p1 != p2) {
				
				game.getItems(p1).remove(item);
				if (p2 != null) {
					game.getItems(p2).add(item);
				}
				
				edits.add(new ItemPassEdit(p1, p2, item));
				
			}			
		}

		return edits;
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
		undoManager.addEdit(getController().new AbortEdit());
	}

	@Override
	public void move(Move move) {

		final Rules rules = getController().getRules();		
		final MovePolicy movePolicy = rules.getMovePolicy();
		
		movePolicy.checkMove(game, gameGraph, move);
		
		CompoundEdit moveEdit = getController().new MoveEdit(
				game.getCurrentPlayer(), game.getCurrentRoundNumber());
		
		// move.seal(): das macht game.getMoves().add(..)		
		
		// Move eintragen
		game.getMoves().add(move);
		
		// Tickets richtig weitergeben
		List<ItemPassEdit> itemPassEdits = passItems(game, movePolicy, move);
		for (ItemPassEdit e : itemPassEdits) {
			moveEdit.addEdit(e);
		}
		moveEdit.end();
		
		// Turn/Current sachen nach Move aktualisieren
		Turn turn = rules.getTurnPolicy().getNextTurn(game, gameGraph);		
		game.setCurrentPlayer(turn.getPlayer());
		game.setCurrentRoundNumber(turn.getRoundNumber());
		
		// GameWin ermitteln
		GameWin win = rules.getGameWinPolicy().isGameWon(game, gameGraph);
		getController().setState(this, (win == GameWin.NO) ? GameStatus.IN_GAME : GameStatus.NOT_IN_GAME, win);
		
		undoManager.addEdit(moveEdit);
	}

}
