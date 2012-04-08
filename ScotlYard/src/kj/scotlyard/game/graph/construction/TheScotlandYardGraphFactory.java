package kj.scotlyard.game.graph.construction;

import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;

public class TheScotlandYardGraphFactory implements ScotlandYardGraphFactory {

	@Override
	public Station createStation() {
		return new Station();
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
