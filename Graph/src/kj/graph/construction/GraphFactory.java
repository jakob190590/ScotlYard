package kj.graph.construction;

import kj.graph.Edge;
import kj.graph.Vertex;

public interface GraphFactory {
	
	Vertex<?, ?> createVertex();
	
	Edge<?> createEdge();

}
