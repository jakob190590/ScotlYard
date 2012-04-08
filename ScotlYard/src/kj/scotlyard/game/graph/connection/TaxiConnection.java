package kj.scotlyard.game.graph.connection;

import kj.scotlyard.game.graph.Connection;
import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;

public class TaxiConnection extends Connection implements ConnectionEdge {

	public TaxiConnection(GameGraph graph) {
		super(graph);
	}

}
