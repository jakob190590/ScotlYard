/*
 * ScotlYard -- A software implementation of the Scotland Yard board game
 * Copyright (C) 2012  Jakob Schöttl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package kj.scotlyard.graphbuilder.builder;

import java.awt.geom.Point2D.Double;
import java.util.HashSet;
import java.util.Set;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;

/**
 * Mit diesem Builder wird der Text einer Description Datei erzeugt!
 * Dieser String kann anschliessend noch in eine Datei geschrieben werden.
 * @author jakob190590
 *
 */
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
