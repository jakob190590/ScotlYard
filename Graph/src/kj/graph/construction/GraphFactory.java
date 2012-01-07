package kj.graph.construction;

import kj.graph.Edge;
import kj.graph.Vertex;

/**
 * Factory fuer die Elemente eines Graphen.
 * @author jakob190590
 *
 */
public interface GraphFactory {
	
	/**
	 * 
	 * @return a new Vertex for the graph
	 */
	Vertex<?, ?> createVertex();
	
	/**
	 * 
	 * @return a new Edge for the graph
	 */
	Edge<?> createEdge();

}
