package kj.graph.construction;

import kj.graph.Edge;
import kj.graph.NormalEdge;
import kj.graph.NormalGraphVertex;
import kj.graph.Vertex;

public class NormalGraphFactory implements GraphFactory {

	@Override
	public Vertex<?, ?> createVertex() {
		return new NormalGraphVertex<>();
	}

	@Override
	public Edge<?> createEdge() {
		return new NormalEdge<>();
	}

}
