package kj.scotlyard.board.util;

import java.util.AbstractList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class ListListModel<E> extends AbstractList<E> implements ListModel<E> {
	
	List<E> list = new LinkedList<>();
	
	Set<ListDataListener> listeners = new HashSet<>();

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	@Override
	public E get(int index) {
		return list.get(index);
	}

	@Override
	public E getElementAt(int index) {
		return list.get(index);
	}
	
	@Override
	public int size() {
		return list.size();
	}

	@Override
	public int getSize() {
		return list.size();
	}
	
	
	// Impl for a modifyable List:
	
	@Override
	public E set(int index, E element) {
		E r = list.set(index, element);
		// TODO inform listeners
		return r;
	}

	@Override
	public void add(int index, E element) {
		list.add(index, element);
		// TODO inform listeners
	}
	
	@Override
	public E remove(int index) {
		E r = list.remove(index);
		// TODO inform listeners
		return r;
	}
}
