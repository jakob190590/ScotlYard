package kj.graph;

import java.util.Set;

public interface Vertex {
	
	/**
	 * Returns a collection of the edges from this vertex.
	 * Changes in the result will change the graph!
	 * @return the edges
	 */
	Set<Edge> getEdges();
	
	/**
	 * Computes the direct neighbors of the vertex.
	 * Changes in the result have no effect on the graph.
	 * @return the direct neighbors
	 */
	Set<Vertex> getNeighbors();
		
}
