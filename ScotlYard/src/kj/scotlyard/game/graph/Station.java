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

import java.util.Set;

public class Station implements StationVertex {
	
	private GameGraph gameGraph;

	public Station(GameGraph gameGraph) {
		this.gameGraph = gameGraph;
	}

	@Override
	public Set<ConnectionEdge> getEdges() {		
		return gameGraph.getGraph().edgesOf(this);
	}

	@Override
	public Set<ConnectionEdge> getEdges(StationVertex otherVertex) {
		return gameGraph.getGraph().getAllEdges(this, otherVertex);
	}

}
