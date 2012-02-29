package kj.scotlyard.game.items;

import kj.scotlyard.game.Move;
import kj.scotlyard.game.player.MrXPiece;

/**
 * Doppelzug-Karte, gilt nur fuer MrX.
 * @author jakob190590
 *
 */
public class DoubleMoveCard extends MultiMoveCard {

	@Override
	public boolean isValidFor(Move move) {
		return (move.getMoves().size() == 2);
	}

}
