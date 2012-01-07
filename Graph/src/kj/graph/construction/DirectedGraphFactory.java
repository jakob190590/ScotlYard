package kj.graph.construction;

import kj.graph.DirectedEdge;
import kj.graph.DirectedGraphVertex;
import kj.graph.Edge;
import kj.graph.Vertex;

public class DirectedGraphFactory implements GraphFactory {

	@Override
	public Vertex<?, ?> createVertex() {
		return new DirectedGraphVertex<>();
	}

	@Override
	public Edge<?> createEdge() {
		return new DirectedEdge<>();
	}

}
