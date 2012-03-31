package kj.scotlyard.game.control;

import javax.swing.undo.UndoManager;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.rules.MovePolicy;
import kj.scotlyard.game.rules.Rules;

public class TheInGameController extends TheAbstractGameController {

	private final UndoManager undoManager;
	
	protected TheInGameController(Game game, GameGraph gameGraph, Rules rules, UndoManager undoManager) {
		super(game, gameGraph, rules);
		this.undoManager = undoManager;
	}

	@Override
	public void equipGameStateRequester(GameStateRequester requester) { }

	@Override
	public GameStatus getStatus() {
		return GameStatus.IN_GAME;
	}
	
	@Override
	public UndoManager getUndoManager() {
		// TODO Auto-generated method stub
		return null;
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
	public void abort() { }

	@Override
	public void move(Move move) {
		
		Game game = getGame();
		GameGraph graph = getGameGraph();
		
		// Move abfertigen (fehlende Params setzen)
		
		
		MovePolicy movePolicy = getRules().getMovePolicy();
		
		movePolicy.checkMove(game, graph, move);		
		move.seal();
		
		// Tickets richtig weitergeben
		changeTicketOwner(game, movePolicy, move);
		
		game.getMoves().add(move);
		
		setWin(getRules().getGameWinPolicy().isGameWon(game, graph)); 		
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

}
