package kj.scotlyard.game.graph.construction;

import kj.scotlyard.game.graph.Station;
import kj.scotlyard.game.graph.connection.BusConnection;
import kj.scotlyard.game.graph.connection.FerryConnection;
import kj.scotlyard.game.graph.connection.TaxiConnection;
import kj.scotlyard.game.graph.connection.UndergroundConnection;

public interface ScotlandYardGraphBuilder {
	
	/**
	 * Der Builder baut eine neue Station und laesst
	 * sich dort nieder.
	 * @return die neue Station
	 */
	Station makeStation();
	
	// Diese Beschreibung gilt analog fuer die restlichen Methoden dieser Art!
	/**
	 * Der Builder baut eine Taxiverbindung zur angegebenen
	 * Station, kehrt aber dann auf seine vorherige Station
	 * zurueck - das heisst sein Arbeitsplatz
	 * (<tt>getCurrentStation()</tt>) aendert sich nicht!
	 * @param station Station, zu der die Taxiverbindung gebaut
	 * werden soll; sollte durch den Builder selbst erstellt
	 * worden sein, und nicht von jemand anderem!
	 * @return die gebaute Taxiverbindung
	 */
	TaxiConnection makeTaxiConnectionTo(Station station);
	BusConnection makeBusConnectionTo(Station station);
	UndergroundConnection makeUndergroundConnectionTo(Station station);
	FerryConnection makeFerryConnectionTo(Station station);
	
	// Diese Beschreibung gilt analog fuer die restlichen Methoden dieser Art!
	/**
	 * Der Builder baut eine Taxiverbindung zu einer neuen
	 * Station, und laesst sich auf letzterer nieder. Mit
	 * <tt>getCurrentStation()</tt> kann die neue Station
	 * abgefragt werden.
	 * @return die neue Taxiverbindung.
	 */
	TaxiConnection makeTaxiConnectionToNewStation();
	BusConnection makeBusConnectionToNewStation();
	UndergroundConnection makeUndergroundConnectionToNewStation();
	FerryConnection makeFerryConnectionToNewStation();
	
	/**
	 * Der Builder geht zur angegebenen Station und laesst
	 * sich dort nieder.
	 * @param station der neue Arbeitsplatz des Builders;
	 * sollte durch den Builder selbst erstellt worden sein,
	 * und nicht von jemand anderem!
	 */
	void gotoStation(Station station);
	
	/**
	 * Die Station, an der sich der Builder gerade aufhaelt.
	 * @return momentaner Arbeitsplatz des Builders
	 */
	Station getCurrentStation();

}
