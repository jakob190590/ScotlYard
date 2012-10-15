/*
 * ScotlYard -- A software implementation of the Scotland Yard board game
 * Copyright (C) 2012  Jakob Schöttl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package kj.scotlyard.board;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;

import kj.scotlyard.board.metadata.GameMetaData;
import kj.scotlyard.board.metadata.ItemTypeMetaData;
import kj.scotlyard.board.util.ActionTools;
import kj.scotlyard.game.model.item.Item;

/**
 * Zeigt Buttons für die festgelegten ItemTypes an,
 * und darauf auch die Anzahl der entsprechenden Items
 * aus der angegebenen Menge von Items.
 * 
 * Dieses Panel ist einzig und allein
 * fuer die Item-Buttons bestimmt.
 * Es duerfen keine anderen Components
 * hinzugefuegt werden!
 */
@SuppressWarnings("serial")
public class ItemSelectionPanel extends JPanel {
	
	private Set<Item> items;
	
	/**
	 * Contains available item types. The
	 * order here determines the order of
	 * <code>itemCounts</code> and
	 * <code>getComponents()</code>!
	 */
	private List<Class<? extends Item>> itemTypes = new ArrayList<>();
	private int[] itemCounts;
	
	private ItemSelectListener selectListener;
	
	// Der Parameter source des ActionEvents steht fuer das ausgewaehlte Item!
	private class SelectItemAction extends AbstractAction {
		private Class<? extends Item> itemType;
		public SelectItemAction(Class<? extends Item> itemType) {
			this.itemType = itemType;
			ItemTypeMetaData itmd = GameMetaData.getForItemType(itemType);
			putValue(NAME, itmd.getName());
			putValue(SMALL_ICON, itmd.getIcon());
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// Naechstbestes Item der vorgegebenen Klasse raussuchen
			for (Item t : items) {
				if (t.getClass() == itemType) {
					if (selectListener != null)
						selectListener.selectItem(t);
					return;
				}
			}
		}
	}

	/**
	 * Create the panel.
	 */
	public ItemSelectionPanel() {
		super(new FlowLayout());
	}
	
	
	public Set<Item> getItems() {
		return items;
	}

	public void setItems(Set<Item> items) {
		this.items = items;
		updateItems();
	}
	
	private void clearItemCounts() {
		for (int i = 0; i < itemCounts.length; i++) {
			itemCounts[i] = 0;
		}
	}
	
	/**
	 * Kann aufgerufen werden, wenn sich die Items im
	 * übergebenen Set ändern. Dann werden die Zahlen auf
	 * den Buttons aktualisiert.
	 */
	public void updateItems() {
		if (items != null) {
			// Items zaehlen
			clearItemCounts();
			for (int i = 0; i < itemTypes.size(); i++) {
				Class<? extends Item> type = itemTypes.get(i);
				for (Item t : items) {
					if (t.getClass() == type) {
						itemCounts[i]++;
					}
				}
				// Buttons anpassen
				Component buttons[] = getComponents();
				((AbstractButton) buttons[i]).setText(String.format("%s (%d)", type.getSimpleName(), itemCounts[i]));
				buttons[i].setEnabled(itemCounts[i] > 0);
			}
		}
	}

	/**
	 * Returns an unmodifiable List of the stated item types.
	 * @return an unmodifiable List
	 */
	public List<Class<? extends Item>> getItemTypes() {
		return Collections.unmodifiableList(itemTypes);
	}
	
	/**
	 * Set the item types.
	 * @param itemTypes
	 */
	public void setItemTypes(List<Class<? extends Item>> itemTypes) {
		List<Class<? extends Item>> types = this.itemTypes;
		types.clear();
		if (itemTypes != null) {
			types.addAll(itemTypes);
		}
		final int count = types.size();
		itemCounts = new int[count];
		// Buttons erstellen
		removeAll();
		for (int i = 0; i < count; i++) {
			Class<? extends Item> type = types.get(i);
			JButton btn = new JButton();
//			btn.setIconTextGap(5);
			btn.setVerticalTextPosition(JButton.BOTTOM); // Text unterhalb des Icons (reicht das schon?)
			btn.setAction(new SelectItemAction(type));
			add(btn);
		}
		
		// Mnemonics vergeben
		ActionTools.assignMnemonicsAutmatically(this);
		
	}

	/**
	 * Convenient method for <code>setItemTypes(List)</code>.
	 * @param itemTypes
	 */
	@SuppressWarnings("unchecked")
	public void setItemTypes(Class<? extends Item>... itemTypes) {
		setItemTypes(Arrays.asList(itemTypes));
	}
	
	
	public ItemSelectListener getSelectListener() {
		return selectListener;
	}

	public void setSelectListener(ItemSelectListener selectListener) {
		this.selectListener = selectListener;
	}

	@Override
	public boolean requestFocusInWindow() {
		// requestFocusInWindow bei erstem Item button, der enabled ist!
		for (Component c : getComponents()) {
			if (c.isEnabled())
				return c.requestFocusInWindow();
		}
		return requestFocusInWindow();
	}
}
