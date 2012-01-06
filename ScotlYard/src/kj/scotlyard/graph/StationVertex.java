package kj.scotlyard.graph;

import java.util.Collection;

import kj.graph.Vertex;
import kj.set.ObjectSet;

public class StationVertex implements Vertex<ConnectionEdge> {

	private ObjectSet<ConnectionEdge> edges;
	
	@Override
	public ObjectSet<ConnectionEdge> getEdges() {
		return edges;
	}

	@Override
	public Collection<Vertex<?>> getNeighbors() {
		// TODO Auto-generated method stub
		return null;
	}

}
