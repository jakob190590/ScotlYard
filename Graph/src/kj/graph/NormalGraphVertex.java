package kj.graph;

import java.util.HashSet;
import java.util.Set;

public class NormalGraphVertex<V extends NormalGraphVertex<?, ?>, E extends NormalEdge<?>> implements Vertex<V, E> {

	private Set<E> edges = new HashSet<>();
	
	@Override
	public Set<E> getEdges() {
		return edges;
	}

	@Override
	public Set<V> getNeighbors() {
		Set<V> neighbors = new HashSet<>();
		for (E e : edges) {
			neighbors.add(e.getOther(this));
		}
		return neighbors;
	}

}
