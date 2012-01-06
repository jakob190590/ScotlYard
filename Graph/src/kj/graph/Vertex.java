package kj.graph;

import java.util.Collection;

import kj.set.ObjectSet;

public interface Vertex<E extends Edge<?>> {
	
	ObjectSet<E> getEdges();
	Collection<Vertex<?>> getNeighbors();
		
}
