package kj.scotlyard.game.graph.construction;

import kj.scotlyard.game.graph.Connection;
import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;

public class TheScotlandYardGraphBuilder implements ScotlandYardGraphBuilder {
	
	private ScotlandYardGraphFactory factory;
	private Station current;

	public TheScotlandYardGraphBuilder(TheScotlandYardGraphFactory factory) {
		this.factory = factory;
	}
	
	private void link(Station stationA, Station stationB,
			Connection connection) {
		// TODO implement		
	}

	@Override
	public Station makeStation() {
		current = factory.createStation();
		return current;
	}

	@Override
	public TaxiConnection makeTaxiConnectionTo(Station station) {
		TaxiConnection conn = factory.createTaxiConnection();
		link(current, station, conn);
		return conn;
	}

	@Override
	public BusConnection makeBusConnectionTo(Station station) {
		BusConnection conn = factory.createBusConnection();
		link(current, station, conn);
		return conn;
	}

	@Override
	public UndergroundConnection makeUndergroundConnectionTo(
			Station station) {
		UndergroundConnection conn = factory.createUndergroundConnection();
		link(current, station, conn);
		return conn;
	}

	@Override
	public FerryConnection makeFerryConnectionTo(Station station) {
		FerryConnection conn = factory.createFerryConnection();
		link(current, station, conn);
		return conn;
	}

	@Override
	public TaxiConnection makeTaxiConnectionToNewStation() {
		Station station = factory.createStation();
		TaxiConnection conn = factory.createTaxiConnection();
		link(current, station, conn);
		gotoStation(station);
		return conn;
	}

	@Override
	public BusConnection makeBusConnectionToNewStation() {
		Station station = factory.createStation();
		BusConnection conn = factory.createBusConnection();
		link(current, station, conn);
		gotoStation(station);
		return conn;
	}

	@Override
	public UndergroundConnection makeUndergroundConnectionToNewStation() {
		Station station = factory.createStation();
		UndergroundConnection conn = factory.createUndergroundConnection();
		link(current, station, conn);
		gotoStation(station);
		return conn;
	}

	@Override
	public FerryConnection makeFerryConnectionToNewStation() {
		Station station = factory.createStation();
		FerryConnection conn = factory.createFerryConnection();
		link(current, station, conn);
		gotoStation(station);
		return conn;
	}

	@Override
	public void gotoStation(Station station) {
		current = station;
	}

	@Override
	public Station getCurrentStation() {
		return current;
	}

}
