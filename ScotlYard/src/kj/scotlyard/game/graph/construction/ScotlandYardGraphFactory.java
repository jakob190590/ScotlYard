package kj.scotlyard.game.graph.construction;

import kj.scotlyard.game.graph.BusConnection;
import kj.scotlyard.game.graph.FerryConnection;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.graph.TaxiConnection;
import kj.scotlyard.game.graph.UndergroundConnection;

public interface ScotlandYardGraphFactory {
	
	StationVertex createStation();
	
	TaxiConnection createTaxiConnection();
	BusConnection createBusConnection();
	UndergroundConnection createUndergroundConnection();
	FerryConnection createFerryConnection();

}
