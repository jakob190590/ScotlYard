package kj.scotlyard.game.graph;

public interface ConnectionEdge {

	StationVertex getOther(StationVertex vertex) throws IllegalArgumentException;
	
}
