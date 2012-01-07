package kj.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import kj.set.SealedCopyOnWriteArrayObjectSet;

public class NormalEdge implements Edge {
	
	protected Vertex a;
	protected Vertex b;
	
	public Vertex getOther(Vertex v) throws IllegalArgumentException {
		if (v == a)
			return b;
		
		if (v == b)
			return a;
		
		throw new IllegalArgumentException("Given vertex have nothing to do with this edge.");
	}

	@Override
	public Set<Vertex> getVertices() {
		
		SealedCopyOnWriteArrayObjectSet<Vertex> vertices = new SealedCopyOnWriteArrayObjectSet<>();
		vertices.add(a);
		vertices.add(b);
		vertices.seal();
		
		return vertices;
	}

	public boolean addVertex(Vertex v) {
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

	public boolean removeVertex(Vertex v) {
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
