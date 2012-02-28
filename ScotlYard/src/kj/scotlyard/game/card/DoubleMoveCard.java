package kj.scotlyard.game.card;

import kj.scotlyard.game.Move;
import kj.scotlyard.game.player.MrXPlayer;

/**
 * Doppelzug-Karte, gilt nur fuer MrX.
 * @author jakob190590
 *
 */
public class DoubleMoveCard extends MultiMoveCard {

	@Override
	public boolean isValidFor(Move move) {
		return ((move.getPlayer() instanceof MrXPlayer)
				&& (move.getNextMove() != null)
				&& (move.getNextMove().getNextMove() == null));
	}

}
