package kj.scotlyard.game.graph;

import java.util.Set;

public interface StationVertex {
	
	Set<ConnectionEdge> getEdges();
	
	Set<ConnectionEdge> getEdges(StationVertex otherVertex);

}
