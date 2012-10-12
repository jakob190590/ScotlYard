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

package kj.scotlyard.game.ai.mrx;

import kj.scotlyard.game.graph.ConnectionEdge;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.model.item.Ticket;

public class Alternative {

	private ConnectionEdge edge1;
	private Class<? extends Ticket> ticketType1;
	private StationVertex vertex1;
	
	private ConnectionEdge edge2;
	private Class<? extends Ticket> ticketType2;
	private StationVertex vertex2;
	public Alternative(ConnectionEdge e1, Class<? extends Ticket> t1, StationVertex v1,
			ConnectionEdge e2, Class<? extends Ticket> t2, StationVertex v2) {
		this.edge1 = e1;
		this.ticketType1 = t1;
		this.vertex1 = v1;
		this.edge2 = e2;
		this.ticketType2 = t2;
		this.vertex2 = v2;
	}
	public Alternative(ConnectionEdge e1, Class<? extends Ticket> t1, StationVertex v1) {
		this(e1, t1, v1, null, null, null);
	}

	
	public ConnectionEdge getEdge1() {
		return edge1;
	}
	public Class<? extends Ticket> getTicketType1() {
		return ticketType1;
	}
	public StationVertex getVertex1() {
		return vertex1;
	}
	public ConnectionEdge getEdge2() {
		return edge2;
	}
	public Class<? extends Ticket> getTicketType2() {
		return ticketType2;
	}
	public StationVertex getVertex2() {
		return vertex2;
	}
	public boolean isDoubleMove() {
		return vertex2 != null;
	}
	
	
	@Override
	public String toString() {
		return isDoubleMove() ? "double move" : "single move";
	}
	
}
