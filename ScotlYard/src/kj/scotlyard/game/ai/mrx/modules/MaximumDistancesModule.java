/*
 * ScotlYard -- A software implementation of the Scotland Yard board game
 * Copyright (C) 2012  Jakob Schöttl, Korbinian Eckstein
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

package kj.scotlyard.game.ai.mrx.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kj.scotlyard.game.ai.mrx.Alternative;
import kj.scotlyard.game.ai.mrx.Rating;
import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.model.GameState;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.util.GameStateExtension;

import org.jgrapht.alg.BellmanFordShortestPath;

public class MaximumDistancesModule implements RatingModule {

	@Override
	public Map<Alternative, Rating> rate(GameState gameState,
			GameGraph gameGraph, Player player, Set<Alternative> alternatives) {
		Map<Alternative, Rating> result = new HashMap<>();
		for (Alternative a : alternatives) {
			StationVertex v = (a.isDoubleMove()) ? a.getVertex2() : a.getVertex1();
			int distances[] = new int[gameState.getDetectives().size()];
			int i = 0;
			for (StationVertex vd : GameStateExtension.getDetectivePositions(gameState)) {
				// TODO korbi, hier brauchen wir dann deine breiten suche, die die ferry conns weglaesst!
				List<ConnectionEdge> path = BellmanFordShortestPath.findPathBetween(gameGraph.getGraph(), v, vd);
				
				// workaround wegen ferry conns:
				
				// zaehlen, wie viele ferry conns der pfad enthaelt
				int nFerryConnections = 0;
				for (ConnectionEdge e : path) {
					if (e instanceof FerryConnection) {
						nFerryConnections++;
					}
				}
				// abschaetzung: distanz ist im schnitt 4 mal (2 x 3 mal, 1 x 5 mal) so lang auf normalem wege
				distances[i] = path.size() + nFerryConnections * 3;
				i++;
			}
			
			double rating = 0;
			for (int d : distances) {
				rating += 1. / (d * d);
			}
			rating /= distances.length;
			result.put(a, new Rating(1 - rating));
		}
		return result;
	}

}
