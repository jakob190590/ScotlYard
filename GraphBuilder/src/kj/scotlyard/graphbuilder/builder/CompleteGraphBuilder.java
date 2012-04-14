package kj.scotlyard.graphbuilder.builder;

import java.awt.geom.Point2D.Double;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.Multigraph;

import kj.scotlyard.game.graph.Connection;
import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;

public class CompleteGraphBuilder extends GraphDescriptionBuilder {
	
	private EdgeFactory<StationVertex, ConnectionEdge> ef = new ClassBasedEdgeFactory<StationVertex, ConnectionEdge>(Connection.class);
	GameGraph g = (GameGraph) new Multigraph<StationVertex, ConnectionEdge>(ef);
	Map<Integer, StationVertex> vertexMap = new HashMap<>();
	Set<JComponent> visualComponents = new HashSet<>();

	@Override
	public void addVertex(Class<? extends StationVertex> vertexType,
			int number, Double position) {
		
		if (vertexType != Station.class) {
			throw new IllegalArgumentException("Unknown vertex type.");
		}
		StationVertex v = new Station(g);
		vertexMap.put(number, v);
		g.addVertex(v);
		
		super.addVertex(vertexType, number, position);
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
		
		// Das darf nur passieren, wenn Angaben gueltig sind:
		super.addEdge(edgeType, vertex1, vertex2);
	}
	
	public GameGraph getGameGraph() {
		return g;
	}
	
	public Set<JComponent> getVisualComponents() {
		return visualComponents;
	}

}
