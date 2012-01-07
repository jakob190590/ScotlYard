package kj.graph.construction;

import kj.graph.Vertex;

/**
 * Dieser Builder definiert ein Interface zum erstellen
 * billiger Graphen.
 * 
 * Es koennen keine spezifischen Kanten erstellt werden
 * (alle werden die gleiche Klasse haben).
 * Der Builder kann nicht garantieren, dass der erstellte
 * Graph zusammenhaengend ist.
 * 
 * Es gibt Methoden, die einen Vertex als Argument erwarten.
 * Dieser Vertex sollte ausnahmslos von diesem Builder erstellt
 * worden sein, nicht von einem anderen Builder oder sonst wem!
 * 
 * @author jakob190590
 *
 */
public interface GraphBuilder {
	
	/**
	 * Der Builder erstellt einen Vertex und laesst
	 * sich darauf nieder.
	 * @return den erstellten Vertex
	 */
	Vertex<?, ?> makeVertex();
	
	/**
	 * Der Builder erstellt einen weiteren Vertex,
	 * und verbindet ihn mit dem, auf dem er sitzt
	 * (bleibt aber sitzen wo er ist).
	 * @return den neu erstellten Vertex
	 */
	Vertex<?, ?> addNeighbor();
	
	/**
	 * Der Builder verbindet den angegebenen Vertex
	 * mit dem, auf dem er sitzt (bleibt aber sitzen 
	 * wo er ist).
	 * @param vertex der neue Nachbarvertex; sollte
	 * durch den Builder selbst erstellt worden sein,
	 * und nicht von jemand anderem!
	 */
	void addNeighbor(Vertex<?, ?> vertex);
	
	/**
	 * Der Builder springt auf den angegebenen Vertex
	 * und laesst sich dort nieder (und kann von dort aus
	 * z.B. wieder Nachbarn hinzufuegen).
	 * @param vertex der neue Arbeitsplatz des Builders;
	 * sollte durch den Builder selbst erstellt worden sein,
	 * und nicht von jemand anderem!
	 */
	void gotoVertex(Vertex<?, ?> vertex);

}
