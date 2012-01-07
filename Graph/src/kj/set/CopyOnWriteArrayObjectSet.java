package kj.set;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * CopyOnWriteArrayObjectSet repraesentiert eine Menge von Objekten. Das impliziert,
 * dass die Menge keine gleichen Elemente und keine <tt>null</tt>-Elemente
 * enthalten darf.
 * 
 * CopyOnWriteArrayObjectSet erweitert CopyOnWriteArraySet, damit haben wir
 * dessen Vorteile: Thread-save und gut zum Iterieren.
 * http://docs.oracle.com/javase/6/docs/api/java/util/concurrent/CopyOnWriteArraySet.html
 * 
 * Ausserdem sind die Elemente geordnet!
 *  
 * @author jakob190590
 *
 * @param <E> Typ der Elemente
 */
public class CopyOnWriteArrayObjectSet<E> extends CopyOnWriteArraySet<E> {

	private static final long serialVersionUID = 1L;

	/**
	 * Diese Methode sollte nicht verwendet werden, wenn insgesamt
	 * mehr als ein Element hinzugefuegt werden soll. Use addAll
	 * instead. Und zwar wegen dem langsamen CopyOnWrite des Arrays.
	 */
	@Override
	public boolean add(E e) {
		if (e == null)
			throw new NullPointerException("Element must not be null.");
		
		return super.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		for (E e : c) {
			if (e == null)
				throw new NullPointerException("No element can be null.");
		}
		
		return super.addAll(c);
	}
	
}
