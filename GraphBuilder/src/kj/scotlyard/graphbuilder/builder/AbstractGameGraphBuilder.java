package kj.scotlyard.graphbuilder.builder;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.Multigraph;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;

/**
 * Ein klein bisschen Vorarbeit fuer alle Builder,  
 * die etwas mit einem GameGraph erzeugen wollen.
 * @author jakob190590
 *
 */
public abstract class AbstractGameGraphBuilder implements GraphBuilder {
	
	/** 
	 * EdgeFactory muss dem Multigraph zwar uebergeben werden,
	 * darf aber nicht genutzt werden, weil sie den Typ der Kanten nicht
	 * bestimmen kann (Taxi-, Bus-, ... Connection?).
	 * Wenn sie trotztdem genutzt wird, wird es eine UnsupportedOperationException geben.
	 */
	private EdgeFactory<StationVertex, ConnectionEdge> ef = new EdgeFactory<StationVertex, ConnectionEdge>() {
		@Override
		public ConnectionEdge createEdge(StationVertex arg0, StationVertex arg1) {
			throw new UnsupportedOperationException("The type of the new edge cannot be " +
					"determined. Please add the edge manually by using addEdge(V, V, E).");
		}
	};
	
	/** Der Graph, der im GameGraph gekapselt wird. */
	protected UndirectedGraph<StationVertex, ConnectionEdge> g = new Multigraph<>(ef);
	
	/** Map fuer Nummer-Vertex-Zuordnung. */
	protected Map<Integer, StationVertex> vertexMap = new HashMap<>();
	
	/** Der -- den Graph enthaltende -- GameGraph. */
	protected GameGraph gg = new GameGraph(g);

}
