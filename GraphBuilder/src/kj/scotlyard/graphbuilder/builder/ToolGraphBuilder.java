package kj.scotlyard.graphbuilder.builder;

import java.awt.geom.Point2D.Double;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;
import kj.scotlyard.graphbuilder.BuilderTool.Edge;
import kj.scotlyard.graphbuilder.BuilderTool.EdgeType;
import kj.scotlyard.graphbuilder.BuilderTool.Vertex;
import kj.scotlyard.graphbuilder.BuilderTool.VertexType;

/**
 * Dieser Builder errichtet die Graph-Representation fuer das BuilderTool!
 * (Wird verwendet in <tt>kj.scotlyard.graphbuilder.BuilderTool</tt> beim 
 * Import einer Description Datei.)
 * @author jakob190590
 *
 */
public class ToolGraphBuilder implements GraphBuilder {

	private List<Vertex> vertList = new Vector<>();
	private Map<Integer, Vertex> vertMap = new HashMap<>();
	
	private List<Edge> edgeList = new Vector<>();
	
	@Override
	public void addVertex(Class<? extends StationVertex> vertexType,
			int number, Double position) {
		
		if (vertexType != Station.class) {
			throw new IllegalArgumentException("Vertex type not supported.");
		}
		Vertex v = new Vertex(VertexType.STATION, number, position);
		vertList.add(v);
		vertMap.put(number, v);
	}

	@Override
	public void addEdge(Class<? extends ConnectionEdge> edgeType, int vertex1,
			int vertex2) {
		
		EdgeType type;
		if (edgeType == TaxiConnection.class) {
			type = EdgeType.TAXI_CONNECTION;
		} else if (edgeType == BusConnection.class) {
			type = EdgeType.BUS_CONNECTION;
		} else if (edgeType == UndergroundConnection.class) {
			type = EdgeType.UNDERGROUND_CONNECTION;
		} else if (edgeType == FerryConnection.class) {
			type = EdgeType.FERRY_CONNECTION;
		} else {
			throw new IllegalArgumentException("Edge type not supported.");
		}
		edgeList.add(new Edge(type, vertMap.get(vertex1), vertMap.get(vertex2)));
	}
	
	public List<Vertex> getVertexList() {
		return vertList;
	}
	
	public List<Edge> getEdgeList() {
		return edgeList;
	}

}
