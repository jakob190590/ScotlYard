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

import java.awt.geom.Point2D.Double;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;

/**
 * Hiermit baut man Graphen oder dessen Darstellung der GUI auf. 
 * Bevor mit <tt>addEdge</tt> zwei Knoten verbunden werden koennen,
 * muessen diese mit <tt>addVertex</tt> hinzugefuegt werden!
 * @author jakob190590
 *
 */
public interface GraphBuilder {
	
	void addVertex(Class<? extends StationVertex> vertexType, int number, Double position);
	
	void addEdge(Class<? extends ConnectionEdge> edgeType, int vertex1, int vertex2);
	
}
