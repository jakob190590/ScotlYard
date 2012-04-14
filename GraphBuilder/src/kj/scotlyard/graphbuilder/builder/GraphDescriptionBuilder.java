package kj.scotlyard.graphbuilder.builder;

import java.awt.geom.Point2D.Double;
import java.util.HashSet;
import java.util.Set;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;

public class GraphDescriptionBuilder implements GraphBuilder {
	
	private class Vertex {
		Class<? extends StationVertex> type;
		int number;
		Double position;
		public Vertex(Class<? extends StationVertex> type, int number, Double position) {
			this.type = type;
			this.number = number;
			this.position = position;
		}
		@Override
		public String toString() {
			return type.getName() + " " + number + " " + position.getX() + " " + position.getY();
		}
	}
	
	private class Edge {
		Class<? extends ConnectionEdge> type;
		int vertex1;
		int vertex2;
		public Edge(Class<? extends ConnectionEdge> type, int vertex1,
				int vertex2) {
			this.type = type;
			this.vertex1 = vertex1;
			this.vertex2 = vertex2;
		}
		@Override
		public String toString() {
			return type.getName() + " " + vertex1 + " " + vertex2;
		}
	}

	private Set<Vertex> vertices = new HashSet<>();
	private Set<Edge> edges = new HashSet<>();
	
	@Override
	public void addVertex(Class<? extends StationVertex> vertexType,
			int number, Double position) {
		
		vertices.add(new Vertex(vertexType, number, position));
	}

	@Override
	public void addEdge(Class<? extends ConnectionEdge> edgeType, int vertex1,
			int vertex2) {
		
		edges.add(new Edge(edgeType, vertex1, vertex2));
	}
	
	// String representation
	public String getDescription() {
		String s = "";
		for (Vertex v : vertices) {
			s += "V " + v + '\n';
		}
		for (Edge e : edges) {
			s += "E " + e + '\n';
		}
		return s;
	}

}
