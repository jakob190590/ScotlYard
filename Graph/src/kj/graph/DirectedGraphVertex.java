package kj.graph;

import java.util.Set;

public class DirectedGraphVertex<V extends DirectedGraphVertex<?, ?>, E extends DirectedEdge<?>> extends NormalGraphVertex<V, E> {
	
	public Set<V> getSuccessors() {
		// TODO
		return null;
	}
	
	public Set<V> getPredecessors() {
		// TODO
		return null;
	}
 
}
