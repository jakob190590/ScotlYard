package kj.scotlyard.game.items;

import kj.scotlyard.game.Move;

public abstract class MultiMoveCard extends Card {

	public abstract boolean isValidFor(Move move);

}
