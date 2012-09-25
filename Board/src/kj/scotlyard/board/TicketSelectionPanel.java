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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.AbstractButton;
import javax.swing.JButton;

import kj.scotlyard.game.model.item.Ticket;

/**
 * Zeigt Buttons für die festgelegten TicketTypes an,
 * und darauf auch die Anzahl der entsprechenden Tickets
 * aus der angegebenen Menge von Tickets.
 * 
 * Dieses Panel ist einzig und allein
 * fuer die Ticket-Buttons bestimmt. 
 * Es duerfen keine anderen Components
 * hinzugefuegt werden!
 */
@SuppressWarnings("serial")
public class TicketSelectionPanel extends JPanel {
	
	private Set<Ticket> tickets;
	
	/**
	 * Contains available ticket types. The
	 * order here determines the order of
	 * <code>ticketCounts</code> and
	 * <code>getComponents()</code>!
	 */
	private List<Class<? extends Ticket>> ticketTypes = new ArrayList<>();
	private int[] ticketCounts;
	
	private TicketSelectListener selectListener;
	
	// Der Parameter source des ActionEvents steht fuer das ausgewaehlte Ticket!	
	private class SelectAction implements ActionListener {
		private Class<? extends Ticket> ticketType;
		public SelectAction(Class<? extends Ticket> ticketType) {
			this.ticketType = ticketType;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// Naechstbestes Ticket der vorgegebenen Klasse raussuchen
			for (Ticket t : tickets) {
				if (t.getClass() == ticketType) {
					if (selectListener != null)
						selectListener.selectTicket(t);
					return;
				}
			}
		}
	}

	/**
	 * Create the panel.
	 */
	public TicketSelectionPanel() {
		super(new FlowLayout());
	}
	
	
	public Set<Ticket> getTickets() {
		return tickets;
	}

	public void setTickets(Set<Ticket> tickets) {
		this.tickets = tickets;
		updateTickets();
	}
	
	private void clearTicketCounts() {
		for (int i = 0; i < ticketCounts.length; i++) {
			ticketCounts[i] = 0;
		}
	}
	
	/**
	 * Kann aufgerufen werden, wenn sich die Tickets im
	 * übergebenen Set ändern. Dann werden die Zahlen auf
	 * den Buttons aktualisiert.
	 */
	public void updateTickets() {
		if (tickets != null) {
			// Tickets zaehlen
			clearTicketCounts();
			for (int i = 0; i < ticketTypes.size(); i++) {
				Class<? extends Ticket> type = ticketTypes.get(i);
				for (Ticket t : tickets) {
					if (t.getClass() == type) {
						ticketCounts[i]++;
					}
				}
				// Buttons anpassen
				Component buttons[] = getComponents();
				((AbstractButton) buttons[i]).setText(String.format("%s (%d)", type.getSimpleName(), ticketCounts[i]));
				buttons[i].setEnabled(ticketCounts[i] > 0);
			}
		}
	}

	/**
	 * Returns an unmodifiable List of the stated ticket types.
	 * @return an unmodifiable List
	 */
	public List<Class<? extends Ticket>> getTicketTypes() {		
		return Collections.unmodifiableList(ticketTypes);
	}
	
	/**
	 * Set the ticket types.
	 * @param ticketTypes
	 */
	public void setTicketTypes(List<Class<? extends Ticket>> ticketTypes) {
		List<Class<? extends Ticket>> types = this.ticketTypes;
		types.clear();
		if (ticketTypes != null) {
			types.addAll(ticketTypes);
		}
		final int count = types.size();
		ticketCounts = new int[count];
		// Buttons erstellen
		removeAll();
		for (int i = 0; i < count; i++) {
			Class<? extends Ticket> type = types.get(i);
			JButton btn = new JButton(type.getSimpleName());
//			btn.setIconTextGap(5);
			btn.setVerticalTextPosition(JButton.BOTTOM); // Text unterhalb des Icons (reicht das schon?)
			// TODO lieber ein schoenes Bild anzeigen
			btn.addActionListener(new SelectAction(type));
			add(btn);
		}		
	}

	/**
	 * Convenient method for <code>setTicketTypes(List)</code>.
	 * @param ticketTypes
	 */
	@SuppressWarnings("unchecked")
	public void setTicketTypes(Class<? extends Ticket>... ticketTypes) {
		setTicketTypes(Arrays.asList(ticketTypes));
	}	
	
	
	public TicketSelectListener getSelectListener() {
		return selectListener;
	}

	public void setSelectListener(TicketSelectListener selectListener) {
		this.selectListener = selectListener;
	}

	@Override
	public boolean requestFocusInWindow() {
		// requestFocusInWindow bei erstem Ticket button, der enabled ist!
		for (Component c : getComponents()) {
			if (c.isEnabled())
				return c.requestFocusInWindow();
		}
		return requestFocusInWindow();
	}
}
