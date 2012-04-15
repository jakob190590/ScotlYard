package kj.scotlyard.graphbuilder.builder;

import java.awt.geom.Point2D.Double;
import java.util.HashMap;
import java.util.Map;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.TheGameGraph;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;

public class GameGraphBuilder implements GraphBuilder {
	
	GameGraph g = new TheGameGraph();
	Map<Integer, StationVertex> vertexMap = new HashMap<>();

	@Override
	public void addVertex(Class<? extends StationVertex> vertexType,
			int number, Double position) {
		
		if (vertexType != Station.class) {
			throw new IllegalArgumentException("Unknown vertex type.");
		}
		StationVertex v = new Station(g);
		vertexMap.put(number, v);
		g.addVertex(v);
	}

	@Override
	public void addEdge(Class<? extends ConnectionEdge> edgeType, int vertex1,
			int vertex2) {
		
		ConnectionEdge e;
		if (edgeType == TaxiConnection.class) {
			e = new TaxiConnection(g);
		} else if (edgeType == BusConnection.class) {
			e = new BusConnection(g);
		} else if (edgeType == UndergroundConnection.class) {
			e = new UndergroundConnection(g);
		} else if (edgeType == FerryConnection.class) {
			e = new FerryConnection(g);
		} else {
			throw new IllegalArgumentException("Unknown edge type.");
		}
		g.addEdge(vertexMap.get(vertex1), vertexMap.get(vertex2), e);
	}
	
	// GameGraph instance
	public GameGraph getGameGraph() {
		return g;
	}

}
