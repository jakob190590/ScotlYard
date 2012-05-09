package kj.scotlyard.game.graph;

import java.util.Set;

import org.jgrapht.UndirectedGraph;

public class GameGraph {

	private UndirectedGraph<StationVertex, ConnectionEdge> graph;
	
	private Set<StationVertex> initialStations;
	
	public GameGraph(UndirectedGraph<StationVertex, ConnectionEdge> graph, Set<StationVertex> initialStations) {
		if (graph == null || initialStations == null) {
			throw new NullPointerException("Graph and Initial Stations must not be null.");
		}
		this.graph = graph;
		this.initialStations = initialStations;
		// TODO wer sorgt dafuer, dass graph und initial stations unmodifiable sind??
	}
	
	public UndirectedGraph<StationVertex, ConnectionEdge> getGraph() {
		return graph;
	}
	
	public Set<StationVertex> getInitialStations() {
		return initialStations;
	}
	
}
