package kj.scotlyard.game.model;

public class TheGame implements Game {

	private class PlayerNumberKey {
		private Player piece;
		private int number;
		public PlayerNumberKey(Player piece, int number) {
			this.piece = piece;
			this.number = number;
		}
		@Override
		public int hashCode() {
			// Von Eclipse erstellt
			final int prime = 31;
			int result = 1;
			result = prime * result + number;
			result = prime * result + ((piece == null) ? 0 : piece.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == this) {				
				return true;
			}
			if (obj instanceof PlayerNumberKey) {
				PlayerNumberKey key = (PlayerNumberKey) obj;
				if (key.piece == piece && key.number == number) {
					return true;
				}
			}
			return false;
		}
		
	}
	
}
