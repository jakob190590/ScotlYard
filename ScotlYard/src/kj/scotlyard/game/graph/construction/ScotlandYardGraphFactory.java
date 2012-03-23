package kj.scotlyard.game.graph.construction;

import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;

public interface ScotlandYardGraphFactory {
	
	StationVertex createStation();
	
	TaxiConnection createTaxiConnection();
	BusConnection createBusConnection();
	UndergroundConnection createUndergroundConnection();
	FerryConnection createFerryConnection();

}
