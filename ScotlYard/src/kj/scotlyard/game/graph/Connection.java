package kj.scotlyard.game.graph;

public abstract class Connection implements ConnectionEdge {

	private GameGraph graph;

	public Connection(GameGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public StationVertex getOther(StationVertex vertex)
			throws IllegalArgumentException {
		
		StationVertex v1 = graph.getEdgeSource(this);
		StationVertex v2 = graph.getEdgeTarget(this);
		
		if (vertex == v1)
			return v2;
		else
			return v1;
	}

}
