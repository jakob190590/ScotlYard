package kj.scotlyard.game.graph;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;

public class TheGameGraph implements GameGraph {

	/** 
	 * EdgeFactory muss dem Multigraph zwar uebergeben werden,
	 * darf aber nicht genutzt werden, weil sie den Typ der Kanten nicht
	 * bestimmen kann (Taxi-, Bus-, ... Connection?).
	 * Wenn sie trotztdem genutzt wird, wird es eine NullPointerException geben.
	 */
	private EdgeFactory<StationVertex, ConnectionEdge> ef = new EdgeFactory<StationVertex, ConnectionEdge>() {
		@Override
		public ConnectionEdge createEdge(StationVertex arg0, StationVertex arg1) {
			return null;
		}
	};
	private UndirectedGraph<StationVertex, ConnectionEdge> graph = new Multigraph<>(ef);
	private boolean sealed = false;

	@Override
	public int degreeOf(StationVertex arg0) {
		return graph.degreeOf(arg0);
	}

	@Override
	public ConnectionEdge addEdge(StationVertex arg0, StationVertex arg1) {
		throw new UnsupportedOperationException("You must specify the edge excplicitly.");
	}

	@Override
	public boolean addEdge(StationVertex arg0, StationVertex arg1,
			ConnectionEdge arg2) {
		return graph.addEdge(arg0, arg1, arg2);
	}

	@Override
	public boolean addVertex(StationVertex arg0) {
		return graph.addVertex(arg0);
	}

	@Override
	public boolean containsEdge(ConnectionEdge arg0) {
		return graph.containsEdge(arg0);
	}

	@Override
	public boolean containsEdge(StationVertex arg0, StationVertex arg1) {
		return graph.containsEdge(arg0, arg1);
	}

	@Override
	public boolean containsVertex(StationVertex arg0) {
		return graph.containsVertex(arg0);
	}

	@Override
	public Set<ConnectionEdge> edgeSet() {
		return graph.edgeSet();
	}

	@Override
	public Set<ConnectionEdge> edgesOf(StationVertex arg0) {
		return graph.edgesOf(arg0);
	}

	@Override
	public Set<ConnectionEdge> getAllEdges(StationVertex arg0,
			StationVertex arg1) {
		return graph.getAllEdges(arg0, arg1);
	}

	@Override
	public ConnectionEdge getEdge(StationVertex arg0, StationVertex arg1) {
		return graph.getEdge(arg0, arg1);
	}

	@Override
	public EdgeFactory<StationVertex, ConnectionEdge> getEdgeFactory() {
		return graph.getEdgeFactory();
	}

	@Override
	public StationVertex getEdgeSource(ConnectionEdge arg0) {
		return graph.getEdgeSource(arg0);
	}

	@Override
	public StationVertex getEdgeTarget(ConnectionEdge arg0) {
		return graph.getEdgeTarget(arg0);
	}

	@Override
	public double getEdgeWeight(ConnectionEdge arg0) {
		return graph.getEdgeWeight(arg0);
	}

	@Override
	public boolean removeAllEdges(Collection<? extends ConnectionEdge> arg0) {
		return graph.removeAllEdges(arg0);
	}

	@Override
	public Set<ConnectionEdge> removeAllEdges(StationVertex arg0,
			StationVertex arg1) {
		return removeAllEdges(arg0, arg1);
	}

	@Override
	public boolean removeAllVertices(Collection<? extends StationVertex> arg0) {
		return graph.removeAllVertices(arg0);
	}

	@Override
	public boolean removeEdge(ConnectionEdge arg0) {
		return graph.removeEdge(arg0);
	}

	@Override
	public ConnectionEdge removeEdge(StationVertex arg0, StationVertex arg1) {
		return graph.removeEdge(arg0, arg1);
	}

	@Override
	public boolean removeVertex(StationVertex arg0) {
		return graph.removeVertex(arg0);
	}

	@Override
	public Set<StationVertex> vertexSet() {
		return graph.vertexSet();
	}

	@Override
	public void seal() {
		if (!sealed ) {
			graph = new UnmodifiableUndirectedGraph<>(graph);
			sealed = true;
		}
	}

}
