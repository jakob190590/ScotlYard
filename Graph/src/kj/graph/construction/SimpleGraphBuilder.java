package kj.graph.construction;

import kj.graph.Edge;
import kj.graph.Vertex;

public class SimpleGraphBuilder implements GraphBuilder {
	
	private GraphFactory graphElementFactory;
	private Vertex<?, ?> currentVertex;
	
	public SimpleGraphBuilder(GraphFactory factory) {
		graphElementFactory = factory;
	}

	@Override
	public Vertex<?, ?> makeVertex() {		
		currentVertex = graphElementFactory.createVertex();
		return currentVertex;
	}

	@Override
	public Vertex<?, ?> addNeighbor() {
		Edge<?> edge = graphElementFactory.createEdge();
		Vertex<?, ?> neighbor = graphElementFactory.createVertex();
		
		edge.getVertices().add(currentVertex);
		edge.getVertices().add(neighbor);
		
		currentVertex.getEdges().add(edge);
		neighbor.getEdges().add(edge);
		
		return neighbor;
	}

	@Override
	public void addNeighbor(Vertex<?, ?> vertex) {
		Edge<?> edge = graphElementFactory.createEdge();
		
		edge.getVertices().add(currentVertex);
		edge.getVertices().add(vertex);
		
		currentVertex.getEdges().add(edge);
		vertex.getEdges().add(edge);
	}

	@Override
	public void gotoVertex(Vertex<?, ?> vertex) {
		currentVertex = vertex;
	}

}
