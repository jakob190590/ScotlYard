package kj.scotlyard.game.graph;

import org.apache.commons.attributes.Sealable;
import org.jgrapht.UndirectedGraph;

public interface GameGraph extends UndirectedGraph<StationVertex, ConnectionEdge>, Sealable {

}
