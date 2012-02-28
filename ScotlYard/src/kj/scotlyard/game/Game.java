package kj.scotlyard.game;

import java.util.List;

import kj.scotlyard.game.graph.StationVertex;
import kj.scotlyard.game.player.DetectivePlayer;
import kj.scotlyard.game.player.MrXPlayer;
import kj.scotlyard.game.player.Player;

/**
 * Das Game speichert den Zustand des Spiels, die Spieler
 * und alle bisherigen Zuege.
 * @author jakob190590
 *
 */
public interface Game extends GameState {
	
	void setMrX(MrXPlayer player);
	
	void setCurrentPlayer(Player player);
	
	void setCurrentRound(int round);
	
	MrXPlayer getMrX();
	List<DetectivePlayer> getDetectives();
	List<Player> getPlayers();
	
	/**
	 * Liefert den Spieler, der momentan an der Reihe ist.
	 * Bei fehlerfreiem <tt>carryOut</tt> wird zum naechsten
	 * Spieler weitergeschaltet.
	 * @return der Spieler, der gerade dran ist
	 */
	Player getCurrentPlayer();
	
	List<Move> getMoves();

	/**
	 * Liefert den letzten Zug des angegebenen Spielers.
	 * Bei einem Multimove wird der erste der Kette zurueckgegeben!
	 * @param player Spieler, dessen letzten Zug wir wissen
	 * wollen
	 * @return den letzten Zug
	 */
	Move getLastMoveOf(Player player);
	
	/**
	 * Liefert die Station, auf der sich der angegebene Spieler
	 * befindet. Bei einem Multimove wird also die Station, die
	 * im letzten Element der Kette gespeichert ist, zurueckgegeben.
	 * @param player Spieler, dessen Position wir wissen wollen
	 * @return Position des angegebenen Spielers
	 */
	StationVertex getPositionOf(Player player);
	
	/**
	 * Fuehrt den angegebenen Zug aus. Ist der Zug ungueltig
	 * wird eine Exception geworfen. Die Gueltigkeit wird
	 * die MovePolicy ueberpruefen.
	 * Bei Erfolg ist der naechste Spieler an der Reihe; dieser
	 * kann dann mit <tt>getCurrentPlayer()</tt> abgefragt
	 * werden.
	 * @param move der auszufuehrende Zug
	 */
	void carryOut(Move move);

}
