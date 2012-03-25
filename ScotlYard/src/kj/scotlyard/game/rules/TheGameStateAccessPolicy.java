package kj.scotlyard.game.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.attributes.DefaultSealable;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.AbstractGameState;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.FullGameState;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.ItemListener;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.MoveListener;
import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.PlayerListener;
import kj.scotlyard.game.model.StateListener;
import kj.scotlyard.game.model.items.Item;

public class TheGameStateAccessPolicy implements GameStateAccessPolicy {
	
	private class PartialMove extends DefaultSealable implements Move {
		
		private Move move;
		
		private List<Move> moves;
		
		/**
		 * Erzeugt einen sealed Move Proxy, der den Zugriff auf den echten Move
		 * einschraenkt. Auf Station und Connection kann dann nicht mehr Zugegriffen
		 * werden (throws IllegalAccessException).
		 * Die "Sub Moves", d.h. die Moves in der Move List werden aber nicht
		 * durchgereicht, sondern muessen dem Konstruktor neu uebergeben werden!
		 * @param moveBehind der echte Move, fuer den dieser PartialMove einen
		 * Proxy darstellt.
		 * @param subMoves die Moves aus der Move List des echten Moves (eventuell auch
		 * wiederum Proxies).
		 */
		public PartialMove(Move moveBehind, Move ...subMoves) {
			move = moveBehind;	
			moves = Arrays.asList(subMoves);
			seal();
		}
		
		@Override
		public void seal() {
			super.seal();
			moves = Collections.unmodifiableList(moves);
		}

		@Override
		public Player getPlayer() {
			return move.getPlayer();
		}

		@Override
		public void setPlayer(Player player) {
			checkSealed();
		}

		@Override
		public int getRoundNumber() {
			return move.getRoundNumber();
		}

		@Override
		public void setRoundNumber(int roundNumber) {
			checkSealed();
			
		}

		@Override
		public int getMoveNumber() {
			return move.getMoveNumber();
		}

		@Override
		public void setMoveNumber(int moveNumber) {
			checkSealed();
		}

		@Override
		public int getMoveIndex() {
			return move.getMoveIndex();
		}

		@Override
		public void setMoveIndex(int moveIndex) {
			checkSealed();
		}

		@Override
		public StationVertex getStation() {
			throw new IllegalAccessException("Move detail cannot be accessed.");
		}

		@Override
		public void setStation(StationVertex station) {
			checkSealed();
		}

		@Override
		public ConnectionEdge getConnection() {
			throw new IllegalAccessException("Move detail cannot be accessed.");
		}

		@Override
		public void setConnection(ConnectionEdge connection) {
			checkSealed();
		}

		@Override
		public Item getItem() {
			return move.getItem();
		}

		@Override
		public void setItem(Item item) {
			checkSealed();
		}

		@Override
		public List<Move> getMoves() {
			return moves;
		}
		
	}
	
	private class DetectivesGameState extends AbstractGameState {

		private GameState gameState;
		
		public DetectivesGameState(GameState gameState) {
			this.gameState = gameState;
		}
		
		@Override
		public MrXPlayer getMrX() {
			return gameState.getMrX();
		}

		@Override
		public List<DetectivePlayer> getDetectives() {
			return gameState.getDetectives();
		}

		@Override
		public List<Player> getPlayers() {
			return gameState.getPlayers();
		}

		@Override
		public Set<Item> getItems(Player player) {
			return gameState.getItems(player);
		}

		@Override
		public List<Move> getMoves() {
			List<Move> list = new Vector<>();
			for (Move m : gameState.getMoves()) {
				list.add(maskMove(m));
			}
			return list;
		}

		@Override
		public Move getMove(Player player, int number, MoveAccessMode accessMode) {
			Move m = gameState.getMove(player, number, accessMode);
			if (accessMode == MoveAccessMode.MOVE_NUMBER) {
				if (getMrXUncoverMoveNumbers().contains(m.getMoveNumber())) {
					return m;
				}
				return new PartialMove(m);
			}
			
			// ... kann jetzt auch Multi Move sein!			
			return maskMove(m);
		}

		@Override
		public Move getLastMove(Player player) {
			return maskMove(gameState.getLastMove(player));
		}

		@Override
		public int getCurrentRoundNumber() {
			return gameState.getCurrentRoundNumber();
		}

		@Override
		public Player getCurrentPlayer() {
			return gameState.getCurrentPlayer();
		}
		
		private Move maskMove(Move move) {
			
			// Move mit dieser MoveNumber ist sichtbar
			// => kein Multi Move (weil die haben keine MoveNumber)
			// -> direkt zurueckgeben!
			if (getMrXUncoverMoveNumbers().contains(move.getMoveNumber())) {
				return move;
			}
			
			// Sonst: Move ist kein Multi Move
			if (move.getMoves().size() == 0) {
				return new PartialMove(move);
			}
			
			Move[] arr = new Move[move.getMoves().size()];
			int i = 0;
			for (Move m : move.getMoves()) {
				if (getMrXUncoverMoveNumbers().contains(m.getMoveNumber())) {
					arr[i] = m;
				} else {
					arr[i] = new PartialMove(m);
				}
				i++;
			}
			return new PartialMove(move, arr);
		}
		
	}

	private final List<Integer> uncoverMoveNumbers;
	{
		List<Integer> list = new ArrayList<>(4);
		list.add(3);
		list.add(8);
		list.add(13);
		list.add(18);
		uncoverMoveNumbers = Collections.unmodifiableList(list);
	}
	
	@Override
	public GameState createGameStateForDetectives(GameState gameState) {
		return new DetectivesGameState(gameState);
	}

	@Override
	public List<Integer> getMrXUncoverMoveNumbers() {		
		return uncoverMoveNumbers;
	}

}
