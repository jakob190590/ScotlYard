package kj.graph;

public class Edge {
	
	private Vertex a;
	private Vertex b;
	private boolean directed;
	
	public Vertex getOther(Vertex v) throws IllegalArgumentException {
		if (v == a)
			return b;
		
		if (v == b)
			return a;
		
		throw new IllegalArgumentException("Given vertex have nothing to do with this edge.");
	}

}
