package kj.scotlyard.game.control;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.model.Game;
import kj.scotlyard.game.rules.GameWin;
import kj.scotlyard.game.rules.Rules;

public abstract class TheAbstractGameController extends GameController {
	
	private Rules rules;
	
	private Game game;
	
	private GameGraph gameGraph;
	
	private GameWin win = GameWin.NO;
	
	protected TheAbstractGameController(Game game, GameGraph gameGraph, Rules rules) {
		this.game = game;
		this.gameGraph = gameGraph;
		this.rules = rules;
	}
	
	protected Game getGame() {
		return game;
	}

	@Override
	public GameGraph getGameGraph() {
		return gameGraph;
	}
		
	@Override
	public void setRules(Rules rules) {
		this.rules = rules;
	}

	@Override
	public Rules getRules() {
		return rules;
	}
	
	@Override
	public GameWin getWin() {
		return win;
	}
	
	protected void setWin(GameWin win) {
		this.win = win;
	}
	
}
