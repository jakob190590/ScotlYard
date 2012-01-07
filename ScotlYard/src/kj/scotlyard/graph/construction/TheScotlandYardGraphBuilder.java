package kj.scotlyard.graph.construction;

import kj.scotlyard.graph.BusConnection;
import kj.scotlyard.graph.ConnectionEdge;
import kj.scotlyard.graph.FerryConnection;
import kj.scotlyard.graph.StationVertex;
import kj.scotlyard.graph.TaxiConnection;
import kj.scotlyard.graph.UndergroundConnection;

public class TheScotlandYardGraphBuilder implements ScotlandYardGraphBuilder {
	
	private ScotlandYardGraphFactory factory;
	private StationVertex current;

	public TheScotlandYardGraphBuilder(TheScotlandYardGraphFactory factory) {
		this.factory = factory;
	}
	
	private void link(StationVertex stationA, StationVertex stationB,
			ConnectionEdge connection) {
		connection.getVertices().add(stationA);
		connection.getVertices().add(stationB);
		
		stationA.getEdges().add(connection);
		stationB.getEdges().add(connection);		
	}

	@Override
	public StationVertex makeStation() {
		current = factory.createStation();
		return current;
	}

	@Override
	public TaxiConnection makeTaxiConnectionTo(StationVertex station) {
		TaxiConnection conn = factory.createTaxiConnection();
		link(current, station, conn);
		return conn;
	}

	@Override
	public BusConnection makeBusConnectionTo(StationVertex station) {
		BusConnection conn = factory.createBusConnection();
		link(current, station, conn);
		return conn;
	}

	@Override
	public UndergroundConnection makeUndergroundConnectionTo(
			StationVertex station) {
		UndergroundConnection conn = factory.createUndergroundConnection();
		link(current, station, conn);
		return conn;
	}

	@Override
	public FerryConnection makeFerryConnectionTo(StationVertex station) {
		FerryConnection conn = factory.createFerryConnection();
		link(current, station, conn);
		return conn;
	}

	@Override
	public TaxiConnection makeTaxiConnectionToNewStation() {
		StationVertex station = factory.createStation();
		TaxiConnection conn = factory.createTaxiConnection();
		link(current, station, conn);
		gotoStation(station);
		return conn;
	}

	@Override
	public BusConnection makeBusConnectionToNewStation() {
		StationVertex station = factory.createStation();
		BusConnection conn = factory.createBusConnection();
		link(current, station, conn);
		gotoStation(station);
		return conn;
	}

	@Override
	public UndergroundConnection makeUndergroundConnectionToNewStation() {
		StationVertex station = factory.createStation();
		UndergroundConnection conn = factory.createUndergroundConnection();
		link(current, station, conn);
		gotoStation(station);
		return conn;
	}

	@Override
	public FerryConnection makeFerryConnectionToNewStation() {
		StationVertex station = factory.createStation();
		FerryConnection conn = factory.createFerryConnection();
		link(current, station, conn);
		gotoStation(station);
		return conn;
	}

	@Override
	public void gotoStation(StationVertex station) {
		current = station;
	}

	@Override
	public StationVertex getCurrentStation() {
		return current;
	}

}
