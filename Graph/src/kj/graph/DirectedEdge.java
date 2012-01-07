package kj.graph;

public class DirectedEdge extends NormalEdge {

	public Vertex getTail() {
		return a;
	}
	
	public Vertex getHead() {
		return b;
	}
	
	public boolean isTail(Vertex v) {
		return (a == v);
	}
	
	public boolean isHead(Vertex v) {
		return (b == v);
	}
	
}
