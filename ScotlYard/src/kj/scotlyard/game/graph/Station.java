package kj.scotlyard.game.graph;

import java.util.Set;

public class Station implements StationVertex {
	
	private GameGraph gameGraph;

	public Station(GameGraph gameGraph) {
		this.gameGraph = gameGraph;
	}

	@Override
	public Set<ConnectionEdge> getEdges() {		
		return gameGraph.getGraph().edgesOf(this);
	}

	@Override
	public Set<ConnectionEdge> getEdges(StationVertex otherVertex) {
		return gameGraph.getGraph().getAllEdges(this, otherVertex);
	}

}
