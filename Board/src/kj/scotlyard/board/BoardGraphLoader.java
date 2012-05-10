package kj.scotlyard.board;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import kj.scotlyard.game.graph.GameGraph;
import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.graphbuilder.builder.Director;

import javax.swing.JComponent;

public class BoardGraphLoader {

	private boolean loaded = false;	
	
	private GameGraph gameGraph;
	private Set<JComponent> visualComponents;
	private Map<Integer, StationVertex> numberStationMap;
	
	/**
	 * Test program
	 * @param args
	 */
	public static void main(String[] args) {
		BoardGraphLoader loader = new BoardGraphLoader();
		try {
			loader.load("graph-description", "initial-stations");
			
			loader.getGameGraph();
			loader.getVisualComponents();
			loader.getNumberStationMap();
			loader.getInitialStations();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkLoaded() {
		if (!loaded) {
			throw new IllegalStateException("You must load first! Call the load method on this GameGraphLoader.");
		}
	}
	
	public void load(String graphDescriptionFilename, String initialStationsFilename) throws IOException {
		
		BoardGraphBuilder builder = new BoardGraphBuilder();
		
		Director.construct(graphDescriptionFilename, builder);
		
		// Products abholen
		gameGraph = builder.getGameGraph();
		visualComponents = builder.getVisualComponents();
		numberStationMap = builder.getNumberStationMap();
		
		
		Set<StationVertex> initialStations = new HashSet<>();
		Scanner sc = new Scanner(new File(initialStationsFilename));
		while (sc.hasNext()) {
			StationVertex station = numberStationMap.get(sc.nextInt());
			if (station == null) {
				throw new RuntimeException("Initial Stations file contains numbers, that are not in the graph."); // TODO
			}
			initialStations.add(station);
		}
		
		gameGraph.setInitialStations(initialStations);
		
		// Seal & release GameState ("make unmodifiable")
		gameGraph.seal();
		
		// Erfolgreich geladen
		loaded = true;
	}
	
	public GameGraph getGameGraph() {
		checkLoaded();
		return gameGraph;
	}
	
	public Set<JComponent> getVisualComponents() {
		checkLoaded();
		return visualComponents;
	}
	
	public Map<Integer, StationVertex> getNumberStationMap() {
		checkLoaded();
		return numberStationMap;
	}
	
	@Deprecated // initial stations are now stored in GameGraph (in addition to the graph)
	public Set<StationVertex> getInitialStations() {
		checkLoaded();
		return gameGraph.getInitialStations();
	}

}
