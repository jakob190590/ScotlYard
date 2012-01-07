package kj.set;

import java.util.Collection;

import kj.utils.Sealable;
import kj.utils.SealedOperationException;

public class SealedCopyOnWriteArrayObjectSet<E> extends
		CopyOnWriteArrayObjectSet<E> implements Sealable {

	private static final long serialVersionUID = 1L;

	private boolean sealed;
	private void checkSealed() {
		if (sealed)
			throw new SealedOperationException();
	}
	
	@Override
	public void seal() {
		sealed = true;
	}

	@Override
	public boolean isSealed() {		
		return sealed;
	}

	// Potentially Sealed Operations
	
	@Override
	public boolean add(E e) {
		checkSealed();
		return super.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		checkSealed();
		return super.addAll(c);
	}

	@Override
	public void clear() {
		checkSealed();
		super.clear();
	}

	@Override
	public boolean remove(Object o) {
		checkSealed();
		return super.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		checkSealed();
		return super.removeAll(c);
	}

}
