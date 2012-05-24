/*
 * ScotlYard -- A software implementation of the Scotland Yard board game
 * Copyright (C) 2012  Jakob Schöttl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package kj.scotlyard.game.control.impl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import kj.scotlyard.game.control.GameStatus;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.rules.GameInitPolicy;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.Rules;
import kj.scotlyard.game.rules.Turn;
import kj.scotlyard.game.rules.TurnPolicy;
import kj.scotlyard.game.util.MoveProducer;

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
	
	@SuppressWarnings("serial")
	private class NewGameEdit extends AbstractUndoableEdit {
		
		private List<Move> oldMoves;
		
		private Player oldCurrentPlayer;
		
		private int oldCurrentRoundNumber;
		
		public NewGameEdit(List<Move> oldMoves, Player oldCurrentPlayer,
				int oldCurrentRoundNumber) {
			this.oldMoves = new ArrayList<>(oldMoves);
			this.oldCurrentPlayer = oldCurrentPlayer;
			this.oldCurrentRoundNumber = oldCurrentRoundNumber;
		}

		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			game.getMoves().addAll(oldMoves);
			game.setCurrentPlayer(oldCurrentPlayer);
			game.setCurrentRoundNumber(oldCurrentRoundNumber);
		}
		
		@Override
		public void redo() throws CannotRedoException {
			super.redo();			
			game.getMoves().clear();
			game.setCurrentPlayer(null);
			game.setCurrentRoundNumber(0);
		}
		
	}
	
	@SuppressWarnings("serial")
	private class ClearPlayersEdit extends AbstractUndoableEdit {
		
		private MrXPlayer oldMrX;
		
		private List<DetectivePlayer> oldDetectives;
		
		public ClearPlayersEdit(MrXPlayer oldMrX, 
				List<DetectivePlayer> oldDetectives) {
			this.oldMrX = oldMrX;
			this.oldDetectives = new ArrayList<>(oldDetectives);
		}

		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			game.setMrX(oldMrX);
			game.getDetectives().addAll(oldDetectives);
		}
		
		@Override
		public void redo() throws CannotRedoException {
			super.redo();			
			game.setMrX(null);
			game.getDetectives().clear();
		}
		
	}
	
	private final Game game;
	
	private final GameGraph gameGraph;
	
	private final Rules rules;
	
	private final UndoManager undoManager;
	
	protected NotInGameControllerState(DefaultGameController controller) {
		super(controller);
		game = controller.getGame();
		gameGraph = controller.getGameGraph();
		rules = controller.getRules();
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
		UndoableEdit edit = new NewGameEdit(game.getMoves(),
				game.getCurrentPlayer(), game.getCurrentRoundNumber());
		
		game.getMoves().clear();
		game.setCurrentPlayer(null);
		game.setCurrentRoundNumber(0); // Einfach der default Wert -- hat nichts mit INITIAL_ROUND_NUMBER zu tun.
		
		undoManager.addEdit(edit);
	}

	@Override
	public void clearPlayers() {
		UndoableEdit edit = new ClearPlayersEdit(game.getMrX(), game.getDetectives());
		
		game.setMrX(null);
		game.getDetectives().clear();
		
		undoManager.addEdit(edit);
	}

	@Override
	public void newMrX() {
		MrXPlayer mrX = game.getMrX();
		game.setMrX(new MrXPlayer());
		undoManager.addEdit(new NewMrXEdit(mrX));
	}

	@Override
	public void newDetective() {
		DetectivePlayer d = new DetectivePlayer();
		game.getDetectives().add(d);
		undoManager.addEdit(new NewDetectiveEdit(d));
	}

	@Override
	public void removeDetective(DetectivePlayer detective) {
		int i = game.getDetectives().indexOf(detective);
		game.getDetectives().remove(detective);
		undoManager.addEdit(new RemoveDetectiveEdit(i, detective));
	}

	@Override
	public void shiftUpDetective(DetectivePlayer detective) {
		List<DetectivePlayer> ds = getController().getGame().getDetectives();
		int i = ds.indexOf(detective);
		if (i < 0) {
			throw new IllegalArgumentException("The specified detective is not part of the game.");
		}
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
		if (i < 0) {
			throw new IllegalArgumentException("The specified detective is not part of the game.");
		}		
		if (i < (ds.size() - 1)) {
			ds.remove(detective);
			ds.add(i + 1, detective);
			undoManager.addEdit(new ShiftDownDetectiveEdit(i, detective));
		}
	}

	@Override
	public void start() {
		
		final GameInitPolicy initPolicy = rules.getGameInitPolicy();
		final TurnPolicy turnPolicy = rules.getTurnPolicy();
		
		if (!game.getMoves().isEmpty()) {
			throw new IllegalStateException("Cannot start game, while Move list is not cleared. Call newGame and try again.");
		}
		
		if (game.getMrX() == null) {
			throw new IllegalStateException("Cannot start game without MrX.");
		}
		
		int dc = game.getDetectives().size();
		int min = initPolicy.getMinDetectiveCount();
		int max = initPolicy.getMaxDetectiveCount();
		if (dc < min || dc > max) {
			throw new IllegalStateException("Cannot start game with " + dc + " detective(s). " +
					"Required: At least " + min + ", at most " + max + ".");
		}
		
		
		// Valid GameState -> proceed with initialization	
		
		Turn turn = turnPolicy.getNextTurn(game, gameGraph);
		
		final int initRoundNumber = turn.getRoundNumber();
		game.setCurrentRoundNumber(initRoundNumber); // should be INITIAL_ROUND_NUMBER
		
		while ((turn = turnPolicy.getNextTurn(game, gameGraph))
				.getRoundNumber() == initRoundNumber) {
			
			Player player = turn.getPlayer();
			game.setCurrentPlayer(player);
			game.setItems(player, initPolicy.createItemSet(game, player));
			
			StationVertex initStation = initPolicy.suggestInitialStation(game, gameGraph, player);
			Move initMove = MoveProducer.createInitialMove(player, initStation);
			
			game.getMoves().add(initMove);
		}
				
		game.setCurrentRoundNumber(turn.getRoundNumber());
		game.setCurrentPlayer(turn.getPlayer());

		
		// Grundsaetzlich kann ein Spiel auch sofort entschieden sein.
		// Beim echten Scotland Yard zwar nicht, weil die moeglichen Startpositionen einen gewissen Abstand 
		// haben und MrX deswegen nicht sofort umzingelt sein kann. Aber es soll ja allgemein sein!
		GameWin win = getController().getRules().getGameWinPolicy().isGameWon(game, gameGraph);
		getController().setState(this, (win == GameWin.NO) ? GameStatus.IN_GAME : GameStatus.NOT_IN_GAME, win);
		
		undoManager.die();		
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
