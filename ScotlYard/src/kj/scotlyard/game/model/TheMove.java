package kj.scotlyard.game.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.attributes.DefaultSealable;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.items.Item;

public class TheMove extends DefaultSealable implements Move {
	
	private Player player;
	
	private int roundNumber;
	
	private int moveNumber;
	
	private int moveIndex;
	
	private StationVertex station;
	
	private ConnectionEdge connection;
	
	private Item item;
	
	private List<Move> moves = new ArrayList<Move>();
	
	public TheMove(Player player, StationVertex station) {
		// Fuer Initial Move
		this.player = player;
		this.station = station;
	}
	
	public TheMove(Player player, ConnectionEdge connection, StationVertex station, Item item) {
		// Fuer normalen Move
		this.player = player;
		this.connection = connection;
		this.station = station;
		this.item = item;
	}
	
	public TheMove(Player player, Item item, Move ...moves) {
		// Fuer Multi Move
		this.player = player;
		this.item = item;
		this.moves.addAll(Arrays.asList(moves));
	}

	@Override
	public void seal() {
		super.seal();
		moves = Collections.unmodifiableList(moves);
	}
	
	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void setPlayer(Player player) {
		checkSealed();
		this.player = player;
	}	

	@Override
	public int getRoundNumber() {
		return roundNumber;
	}

	@Override
	public void setRoundNumber(int roundNumber) {
		checkSealed();
		this.roundNumber = roundNumber;
	}

	@Override
	public int getMoveNumber() {
		return moveNumber;
	}

	@Override
	public void setMoveNumber(int moveNumber) {
		checkSealed();
		this.moveNumber = moveNumber;
	}

	@Override
	public int getMoveIndex() {
		return moveIndex;
	}

	@Override
	public void setMoveIndex(int moveIndex) {
		checkSealed();
		this.moveIndex = moveIndex;
	}

	@Override
	public StationVertex getStation() {
		return station;
	}

	@Override
	public void setStation(StationVertex station) {
		checkSealed();
		this.station = station;
	}

	@Override
	public ConnectionEdge getConnection() {
		return connection;
	}

	@Override
	public void setConnection(ConnectionEdge connection) {
		checkSealed();
		this.connection = connection;
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public void setItem(Item item) {
		checkSealed();
		this.item = item;
	}

	@Override
	public List<Move> getMoves() {
		return moves;
	}

}
