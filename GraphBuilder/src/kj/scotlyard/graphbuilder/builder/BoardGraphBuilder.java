package kj.scotlyard.graphbuilder.builder;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import org.jgrapht.graph.UnmodifiableUndirectedGraph;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;

public class BoardGraphBuilder extends AbstractGameGraphBuilder implements GraphBuilder {
	
	/** Gibt an, ob der Graph bereits unmodifiable gemacht wurde. */
	protected boolean unmodifiable = false;
	
	/** Visuelle Komponenten, die Board fuer die GUI benoetigt. */
	protected Set<JComponent> visualComponents = new HashSet<>();

	@Override
	public void addVertex(Class<? extends StationVertex> vertexType,
			int number, Point2D.Double position) {
		
		if (vertexType != Station.class) {
			throw new IllegalArgumentException("Unknown vertex type.");
		}
		StationVertex v = new Station(gg);
		vertexMap.put(number, v);
		g.addVertex(v);
		
	}

	@Override
	public void addEdge(Class<? extends ConnectionEdge> edgeType, int vertex1,
			int vertex2) {
		
		ConnectionEdge e;
		if (edgeType == TaxiConnection.class) {
			e = new TaxiConnection(gg);
		} else if (edgeType == BusConnection.class) {
			e = new BusConnection(gg);
		} else if (edgeType == UndergroundConnection.class) {
			e = new UndergroundConnection(gg);
		} else if (edgeType == FerryConnection.class) {
			e = new FerryConnection(gg);
		} else {
			throw new IllegalArgumentException("Unknown edge type.");
		}
		g.addEdge(vertexMap.get(vertex1), vertexMap.get(vertex2), e);
		
	}
	
	public GameGraph getGameGraph() {
		if (!unmodifiable) {
			// Unmodifiable GameGraph einmalig erzeugen
			gg = new GameGraph(new UnmodifiableUndirectedGraph<StationVertex, ConnectionEdge>(g));
			unmodifiable = true;
		}
		
		return gg;
	}
	
	public Set<JComponent> getVisualComponents() {
		return visualComponents;
	}
	
	public Map<Integer, StationVertex> getNumberStationMap() {
		return vertexMap;
	}

}
