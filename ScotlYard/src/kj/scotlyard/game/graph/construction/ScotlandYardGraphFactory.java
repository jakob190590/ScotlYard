package kj.scotlyard.game.graph.construction;

import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;

public interface ScotlandYardGraphFactory {
	
	Station createStation();
	
	TaxiConnection createTaxiConnection();
	BusConnection createBusConnection();
	UndergroundConnection createUndergroundConnection();
	FerryConnection createFerryConnection();

}
