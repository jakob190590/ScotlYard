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

import java.awt.geom.Point2D;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;

/**
 * Erzeugt zwei verschiedene GameGraph-Objekte:
 * Einen mit einem veraenderbaren, einen mit einem
 * unveraenderbaren Graphen. Die Produkte koennen
 * abgeholt werden mit <tt>getGameGraph</tt>.
 * @author jakob190590
 *
 */
public class GameGraphBuilder extends AbstractGameGraphBuilder implements GraphBuilder {
		
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

}
