package kj.scotlyard.game.rules;

import kj.scotlyard.game.Move;

public interface MovePolicy {
	
	/** 
	 * Ueberprueft die Gueltigkeit des Zugs. Wenn der Zug
	 * ungueltig ist, muss eine entsprechende Exception
	 * geworfen werden! Zur Ueberpruefung muss natuerlich
	 * irgendwie der Zustand des Spiels abgefragt werden
	 * koennen. Mein Vorschlag ist, das Game-Objekt dem
	 * Konstruktor zu uebergeben der implementierenden
	 * Klasse zu uebergeben.
	 * @param move der zu validierende Zug
	 */
	void checkValidness(Move move); // TODO throws declaration

}
