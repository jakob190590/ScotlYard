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

package kj.scotlyard.game.graph;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.attributes.DefaultSealable;
import org.apache.commons.attributes.Sealable;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;

/**
 * This class is a wrapper around the (game) graph:
 * Graph and initial stations.
 * 
 * Beide Felder muessen gesetzt werden, aber das geht
 * leider nicht durch den Konstruktor, sondern nur mit
 * den Settern. Wenn der GameGraph fertig ist muss er
 * mit der Methode <tt>seal</tt> "sealed & released"
 * werden. Fuer Hintergruende: Entwicklerhandbuch.
 * 
 * @author jakob190590
 *
 */
public class GameGraph extends DefaultSealable implements Sealable {

	private UndirectedGraph<StationVertex, ConnectionEdge> graph;
	
	private Set<StationVertex> initialStations;
	
	public GameGraph() { }

	/**
	 * Seals and releases the GameGraph. Both graph and initial stations
	 * must be set. After this, both will be unmodifiable (in every respect).
	 * @throws NullPointerException if graph or initial stations is <tt>null</tt>.
	 */
	@Override
	public void seal() throws NullPointerException {
		// To seal and release this GameGraph		
		if (graph == null || initialStations == null) {
			throw new NullPointerException("GameGraph cannot be sealed and " +
					"released: Graph and Initial Stations must not be null.");
		}
		
		graph = new UnmodifiableUndirectedGraph<>(graph);
		initialStations = Collections.unmodifiableSet(initialStations);
		super.seal();
	}
	
	public void setGraph(UndirectedGraph<StationVertex, ConnectionEdge> graph) {
		checkSealed();
		this.graph = graph;
	}

	public UndirectedGraph<StationVertex, ConnectionEdge> getGraph() {
		return graph;
	}
	
	public void setInitialStations(Set<StationVertex> initialStations) {
		checkSealed();
		this.initialStations = initialStations;
	}
	
	public Set<StationVertex> getInitialStations() {
		return initialStations;
	}
	
}
