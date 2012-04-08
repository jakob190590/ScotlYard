package kj.scotlyard.game.control;

import java.util.Observable;

import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.IllegalMoveException;

public abstract class GameController extends Observable {
	
	public abstract GameStatus getStatus();
	
	public abstract GameWin getWin();
	

	public abstract void newGame();
	
	public abstract void clearPlayers();
	
	
	public abstract void newMrX();
	
	public abstract void newDetective();
	
	public abstract void removeDetective(DetectivePlayer detective);
	
	public abstract void shiftUpDetective(DetectivePlayer detective);
	
	public abstract void shiftDownDetective(DetectivePlayer detective);
	
	
	public abstract void start();
	
	public abstract void abort();
	
	
	public abstract void move(Move move) throws IllegalMoveException;
	
}
