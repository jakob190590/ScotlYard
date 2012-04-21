package kj.scotlyard.game.control.impl;

import kj.scotlyard.game.control.GameController;
import kj.scotlyard.game.rules.GameWin;

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
