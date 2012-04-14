package kj.scotlyard.graphbuilder.builder;

import java.awt.geom.Point2D.Double;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;

/**
 * Hiermit baut man Graphen oder dessen Darstellung der GUI auf. 
 * Bevor mit <tt>addEdge</tt> zwei Knoten verbunden werden koennen,
 * muessen diese mit <tt>addVertex</tt> hinzugefuegt werden!
 * @author jakob190590
 *
 */
public interface GraphBuilder {
	
	void addVertex(Class<? extends StationVertex> vertexType, int number, Double position);
	
	void addEdge(Class<? extends ConnectionEdge> edgeType, int vertex1, int vertex2);
	
}
