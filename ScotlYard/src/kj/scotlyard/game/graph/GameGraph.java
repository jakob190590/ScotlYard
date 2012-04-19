package kj.scotlyard.game.graph;

import org.jgrapht.UndirectedGraph;

public class GameGraph {

	private UndirectedGraph<StationVertex, ConnectionEdge> graph;
	
	public GameGraph(UndirectedGraph<StationVertex, ConnectionEdge> graph) {
		if (graph == null) {
			throw new NullPointerException("Graph must not be null.");
		}
		this.graph = graph;
	}
	
	public UndirectedGraph<StationVertex, ConnectionEdge> getGraph() {
		return graph;
	}
	
}
