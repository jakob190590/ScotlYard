package kj.scotlyard.game.model.items;

import kj.scotlyard.game.graph.ConnectionType;

public class Ticket implements Item {
	
	private ConnectionType connectionType;
	
	public ConnectionType getConnectionType() {
		return connectionType;
	}

	public Ticket(ConnectionType connectionType) {
		this.connectionType = connectionType;
	}

}
