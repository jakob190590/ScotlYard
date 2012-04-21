package kj.scotlyard.game.rules;

import kj.scotlyard.game.model.Player;

public class Turn {

	private Player currentPlayer;
	
	private int currentRoundNumber;

	public Turn(Player currentPlayer, int currentRoundNumber) {
		this.currentPlayer = currentPlayer;
		this.currentRoundNumber = currentRoundNumber;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public int getCurrentRoundNumber() {
		return currentRoundNumber;
	}
	
}
