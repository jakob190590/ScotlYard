package kj.scotlyard.game.card;

import kj.scotlyard.game.Move;

public abstract class MultiMoveCard extends Card {

	public abstract boolean isValidFor(Move move);

}
