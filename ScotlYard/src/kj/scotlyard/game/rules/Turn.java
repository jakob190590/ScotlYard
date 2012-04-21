package kj.scotlyard.game.rules;

import kj.scotlyard.game.model.Player;

public class Turn {

	private Player player;
	
	private int roundNumber;

	public Turn(Player player, int roundNumber) {
		this.player = player;
		this.roundNumber = roundNumber;
	}

	public Player getPlayer() {
		return player;
	}

	public int getRoundNumber() {
		return roundNumber;
	}
	
}
