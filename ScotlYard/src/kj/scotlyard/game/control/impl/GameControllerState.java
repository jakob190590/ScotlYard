package kj.scotlyard.game.control.impl;

import kj.scotlyard.game.control.GameController;
import kj.scotlyard.game.rules.GameWin;

abstract class GameControllerState extends GameController {
	
	private DefaultGameController controller;

	protected GameControllerState(DefaultGameController controller) {
		this.controller = controller;
	}
	
	protected DefaultGameController getController() {
		return controller;
	}

	@Override
	public GameWin getWin() {
		return controller.getWin();
	}
	
}
