package kj.utils;

public interface Sealable {
	
	/**
	 * Objekt versiegeln, so dass keine Aenderungen mehr
	 * gemacht werden koennen. Kann nicht rueckgaengig
	 * gemacht werden.
	 */
	void seal();
	
	/**
	 * Abfragen, ob Klasse versiegelt ist.
	 * @return <tt>true</tt>, wenn keine Aenderungen mehr
	 * am Objekt gemacht werden koennen.
	 */
	boolean isSealed();

}
