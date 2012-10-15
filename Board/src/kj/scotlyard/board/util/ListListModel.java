package kj.scotlyard.board.util;

import java.util.AbstractList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ListListModel<E> extends AbstractList<E> implements ListModel<E> {
	
	private List<E> list = new LinkedList<>();
	
	private Set<ListDataListener> listeners = new HashSet<>();
	
	private ListDataListener listDataEventDispatcher = new ListDataListener() {
		@Override
		public void intervalRemoved(ListDataEvent e) {
			for (ListDataListener l : listeners) {
				l.intervalRemoved(e);
			}
		}
		@Override
		public void intervalAdded(ListDataEvent e) {
			for (ListDataListener l : listeners) {
				l.intervalAdded(e);
			}
		}
		@Override
		public void contentsChanged(ListDataEvent e) {
			for (ListDataListener l : listeners) {
				l.contentsChanged(e);
			}
		}
	};

	private void dispatchListDataEvent(ListDataEvent e) {
		switch (e.getType()) {
		case ListDataEvent.CONTENTS_CHANGED:
			listDataEventDispatcher.contentsChanged(e);
			break;
		case ListDataEvent.INTERVAL_ADDED:
			listDataEventDispatcher.intervalAdded(e);
			break;
		case ListDataEvent.INTERVAL_REMOVED:
			listDataEventDispatcher.intervalRemoved(e);
			break;
		}
	}
	
	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
	

	// Read-only
	
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
	
	
	// Impl for a modifyable List
	
	@Override
	public E set(int index, E element) {
		E r = list.set(index, element);
		dispatchListDataEvent(new ListDataEvent(this,
				ListDataEvent.CONTENTS_CHANGED, index, index));
		return r;
	}

	@Override
	public void add(int index, E element) {
		list.add(index, element);
		dispatchListDataEvent(new ListDataEvent(this,
				ListDataEvent.INTERVAL_ADDED, index, index));
	}
	
	@Override
	public E remove(int index) {
		E r = list.remove(index);
		dispatchListDataEvent(new ListDataEvent(this,
				ListDataEvent.INTERVAL_REMOVED, index, index));
		return r;
	}
}
