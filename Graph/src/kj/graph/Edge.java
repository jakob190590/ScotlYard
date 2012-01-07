package kj.graph;

import java.util.Set;

public interface Edge {
	
	/**
	 * Returns a collection of the vertices from this edge.
	 * Normally an edge has two vertices; a hypergraph can
	 * have more than two!
	 * 
	 * Changes in the result will change the graph!
	 * 
	 * @return the vertices
	 */
	Set<Vertex> getVertices();		
	
}
