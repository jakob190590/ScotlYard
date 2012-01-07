package kj.scotlyard.graph.construction;

import kj.scotlyard.graph.BusConnection;
import kj.scotlyard.graph.FerryConnection;
import kj.scotlyard.graph.StationVertex;
import kj.scotlyard.graph.TaxiConnection;
import kj.scotlyard.graph.UndergroundConnection;

public class TheScotlandYardGraphFactory implements ScotlandYardGraphFactory {

	@Override
	public StationVertex createStation() {
		return new StationVertex();
	}

	@Override
	public TaxiConnection createTaxiConnection() {
		return new TaxiConnection();
	}

	@Override
	public BusConnection createBusConnection() {
		return new BusConnection();
	}

	@Override
	public UndergroundConnection createUndergroundConnection() {
		return new UndergroundConnection();
	}

	@Override
	public FerryConnection createFerryConnection() {
		return new FerryConnection();
	}

}
