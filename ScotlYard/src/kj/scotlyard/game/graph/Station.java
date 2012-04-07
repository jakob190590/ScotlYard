package kj.scotlyard.game.graph;

import java.util.Set;

public class Station implements StationVertex {
	
	private GameGraph graph;

	public Station(GameGraph graph) {
		this.graph = graph;
	}

	@Override
	public Set<ConnectionEdge> getEdges() {		
		return graph.edgesOf(this);
	}

	@Override
	public Set<ConnectionEdge> getEdges(StationVertex otherVertex) {
		return graph.getAllEdges(this, otherVertex);
	}

}
