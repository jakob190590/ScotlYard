package kj.scotlyard.game.model;

import java.util.List;


public interface GameState {
	
	enum MoveAccessMode {
        ROUND,
        MOVENUMBER
    }
	
	class PieceIntKey {
		private PlayingPiece piece;
		private int i;
		public PieceIntKey(PlayingPiece piece, int i) {
			super();
			this.piece = piece;
			this.i = i;
		}
		@Override
		public int hashCode() {
			super.hashCode();
			final int prime = 31;
			int result = 1;
			result = prime * result + i;
			result = prime * result + ((piece == null) ? 0 : piece.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == this) {				
				return true;
			}
			if (obj instanceof PieceIntKey) {
				PieceIntKey key = (PieceIntKey) obj;
				if (key.piece == piece && key.i == i) {
					return true;
				}
			}
			return false;
		}
		
	}
	
	int INITIAL_ROUND = 0;
	
	MrXPiece getMrX();
	
	List<DetectivePiece> getDetectives();
	
	List<PlayingPiece> getPlayers();
	
	List<Move> getMoves();
	
	int getCurrentRound();
	
	PlayingPiece getCurrentPlayer();
	
	// Convenient methods
	
	List<Move> getMovesOfRound(int round);
	
	List<Move> getMovesOfCurrentRound();
	
	Move getMove(PlayingPiece player, int round);
	
	Move getLastMove(PlayingPiece player);
	
	// To add and remove Listeners

}
