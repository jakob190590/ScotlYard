package kj.set;

import java.util.HashSet;

/**
 * ObjectSet repraesentiert eine Menge von Objekten. Das impliziert,
 * dass die Menge keine gleichen Elemente und keine <tt>null</tt>-Elemente
 * enthalten darf.
 * 
 * @author jakob190590
 *
 * @param <E> Klasse der Objekte.
 */
public class ObjectSet<E> extends HashSet<E> {
	
	private static final long serialVersionUID = 1L;

	@Override
	public boolean add(E e) {
		if (e == null)
			throw new NullPointerException("Element must not be null.");
		
		return super.add(e);
	}
}
