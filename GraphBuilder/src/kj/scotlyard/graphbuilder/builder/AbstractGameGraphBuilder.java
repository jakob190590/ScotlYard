/*
 * ScotlYard -- A software implementation of the Scotland Yard board game
 * Copyright (C) 2012  Jakob Schöttl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
	protected GameGraph gg = new GameGraph();
	{
		gg.setGraph(g);
	}
	
	/** Zum Abholen des Produkts. */
	public GameGraph getGameGraph() {
		return gg;
	}

}
