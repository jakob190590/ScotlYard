package kj.scotlyard.game.graph;

public abstract class Connection implements ConnectionEdge {

	private GameGraph gameGraph;

	public Connection(GameGraph gameGraph) {
		this.gameGraph = gameGraph;
	}
	
	@Override
	public StationVertex getOther(StationVertex vertex)
			throws IllegalArgumentException {
		
		StationVertex v1 = gameGraph.getGraph().getEdgeSource(this);
		StationVertex v2 = gameGraph.getGraph().getEdgeTarget(this);
		
		if (vertex == v1)
			return v2;
		else
			return v1;
	}

}
