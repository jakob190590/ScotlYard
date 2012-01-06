package kj.graph;

public class DirectedEdge<V extends Vertex<?>> extends NormalEdge<V> {

	public V getTail() {
		return a;
	}
	
	public V getHead() {
		return b;
	}
	
	public boolean isTail(V v) {
		return (a == v);
	}
	
	public boolean isHead(V v) {
		return (b == v);
	}
	
}
