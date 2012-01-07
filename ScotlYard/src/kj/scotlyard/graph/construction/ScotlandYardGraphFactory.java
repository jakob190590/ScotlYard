package kj.scotlyard.graph.construction;

import kj.scotlyard.graph.BusConnection;
import kj.scotlyard.graph.FerryConnection;
import kj.scotlyard.graph.StationVertex;
import kj.scotlyard.graph.TaxiConnection;
import kj.scotlyard.graph.UndergroundConnection;

public interface ScotlandYardGraphFactory {
	
	StationVertex createStation();
	
	TaxiConnection createTaxiConnection();
	BusConnection createBusConnection();
	UndergroundConnection createUndergroundConnection();
	FerryConnection createFerryConnection();

}
