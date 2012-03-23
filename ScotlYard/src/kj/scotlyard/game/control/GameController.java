package kj.scotlyard.game.control;

import java.util.Observable;

import javax.swing.undo.UndoManager;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.Rules;

public abstract class GameController extends Observable {
	
	public abstract void setRules(Rules rules);

	public abstract Rules getRules();
	
	
	public abstract void equipGameStateRequester(GameStateRequester requester);	
	
	public abstract GameGraph getGameGraph();
	
	
	public abstract GameStatus getStatus();
	
	public abstract GameWin getWin();
	

	public abstract UndoManager getUndoManager();
	
		
	public abstract void newGame();
	
	public abstract void clearPlayers();
	
	
	public abstract void newMrX();
	
	public abstract void newDetective();
	
	public abstract void removeDetective(DetectivePlayer detective);
	
	public abstract void shiftUpDetective(DetectivePlayer detective);
	
	public abstract void shiftDownDetective(DetectivePlayer detective);
	
	
	public abstract void start();
	
	public abstract void abort();
	
	
	public abstract void move(Move move);
	
}
