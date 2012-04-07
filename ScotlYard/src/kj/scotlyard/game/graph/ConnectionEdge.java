package kj.scotlyard.game.graph;

public interface ConnectionEdge {

	StationVertex getOther(StationVertex station) throws IllegalArgumentException;
	
}
