package kj.graph;

import java.util.Collection;

public interface Edge<V extends Vertex<?>> {
	
	Collection<V> getVertices();
	boolean addVertex(V v) throws UnsupportedOperationException;
	boolean removeVertex(V v);
	

}
