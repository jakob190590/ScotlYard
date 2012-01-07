package kj.graph;

import java.util.Set;

import kj.set.SealedCopyOnWriteArrayObjectSet;

public class NormalEdge<V extends NormalGraphVertex<?, ?>> implements Edge<V> {
	
	protected V a;
	protected V b;
	
	public V getOther(V v) throws IllegalArgumentException {
		if (v == a)
			return b;
		
		if (v == b)
			return a;
		
		throw new IllegalArgumentException("Given vertex have nothing to do with this edge.");
	}

	@Override
	public Set<V> getVertices() {
		
		SealedCopyOnWriteArrayObjectSet<V> vertices = new SealedCopyOnWriteArrayObjectSet<>();
		vertices.add(a);
		vertices.add(b);
		vertices.seal();
		
		return vertices;
	}

	public boolean addVertex(V v) {
		if (v == null)
			throw new NullPointerException("Argument must not be null.");
		
		if (a == null)
			a = v;
		else if (b == null)
			b = v;
		else
			throw new UnsupportedOperationException("A normal edge can link only two vertices.");
		
		return true;
	}

	public boolean removeVertex(V v) {
		if (v != null) {
			
			if (v == a)
				a = null;
			else if (v == b)
				b = null;
			
			return true;
		}
		else
			return false;
	}
	
}
/*
class NormalEdgeVertexSet<E> implements Set<E> {

	private static final int SIZE = 2;
	private E[] els = new E[SIZE];
	private int size = 0;
	
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {		
		return (size == 0);
	}

	@Override
	public boolean contains(Object o) {
		return (els[0].equals(o) || els[1].equals(o));
	}

	@Override
	public Iterator iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		return els.clone();
	}

	@Override
	public Object[] toArray(Object[] a) {
	}

	@Override
	public boolean add(E e) {
		if (size < SIZE) {
			els[size] = e;
			size++;
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		size = 0;
	}
	
}
*/
