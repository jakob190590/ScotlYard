package kj.graph;

import java.util.Set;

/**
 * This class represents one edge of a graph.
 * It is a general interface configurable with
 * the special class of Vertex for method 
 * return values.
 * @author jakob190590
 *
 * @param <V> special class of Vertex
 */
public interface Edge<V extends Vertex<?, ?>> {
	
	/**
	 * Returns a collection of the vertices from this edge.
	 * Normally an edge has two vertices; a hypergraph can
	 * have more than two!
	 * 
	 * Changes in the result will change the graph!
	 * 
	 * @return the vertices
	 */
	Set<V> getVertices();		
	
}
