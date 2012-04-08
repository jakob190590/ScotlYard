package kj.scotlyard.game.graph.connection;

import kj.scotlyard.game.graph.Connection;
import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;

public class BusConnection extends Connection implements ConnectionEdge {

	public BusConnection(GameGraph graph) {
		super(graph);
	}

}
