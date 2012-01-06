package kj.graph;

import java.util.Collection;

public class NormalEdge<V extends Vertex<?>> implements Edge<V> {
	
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
	public Collection<V> getVertices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addVertex(V v) throws UnsupportedOperationException {
		if (v == null)
			throw new NullPointerException("Argument must not be null.");
		
		if (a == null)
			a = v;
		else if (b == null)
			b = v;
		else
			throw new UnsupportedOperationException("Edge can link only two vertices.");
		
		return true;
	}

	@Override
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
