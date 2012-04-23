package kj.scotlyard.game.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.item.Ticket;

/**
 * Builder interface for a Sub Move.
 * An instance should be passed to MoveProducer's 
 * method createMultiMove as parameter.
 * 
 * This builder is designed for simple use like this:
 * <tt>SubMoves sm = new SubMoves().add(...).add(...);</tt>
 * 
 * @author jakob190590
 *
 */
public class SubMoves implements Iterable<Move> {
	
	private List<Move> subMoves = new LinkedList<>();
	
	public SubMoves add(StationVertex station, ConnectionEdge connection, Ticket ticket) {
		
		// Player and Numbers will be set later in createMultiMove
		subMoves.add(new DefaultMove(null, 0, Move.NO_MOVE_NUMBER, 
				Move.NO_MOVE_INDEX, station, connection, ticket));
				
		return this;
	}

	@Override
	public Iterator<Move> iterator() {		
		return subMoves.iterator();
	}

}
