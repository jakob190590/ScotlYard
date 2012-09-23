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

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JLabel;

import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.Ticket;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;

@SuppressWarnings("serial")
public class TicketSelectionPanel extends JPanel {
	
	private Player player;
	private Set<Ticket> tickets;
	/** Contains available ticket types. The
	 * order here determines the order of
	 * <code>ticketCounts</code> and
	 * <code>pnlTickets.getComponents()</code>!
	 */
	private List<Class<? extends Ticket>> ticketTypes = new ArrayList<>();
	private int[] ticketCounts;
	
	/**
	 * Dieses Panel ist einzig und allein
	 * fuer die Ticket-Buttons bestimmt. 
	 * Es duerfen keine andere Components
	 * hinzugefuegt werden!
	 */
	private JPanel pnlTickets;
	
	// Der Parameter source des ActionEvents steht fuer das ausgewaehlte Ticket!
	private TicketSelectListener selectListener;
	private JLabel lblSelectATicket;
	private JCheckBox chckbxFurtherMoves;
	private JButton btnCancel;
	
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
		setLayout(new BorderLayout(0, 0));
		
		pnlTickets = new JPanel();
		add(pnlTickets, BorderLayout.CENTER);
		
		JPanel pnlFooter = new JPanel();
		add(pnlFooter, BorderLayout.SOUTH);
		pnlFooter.setLayout(new BoxLayout(pnlFooter, BoxLayout.X_AXIS));
		
		chckbxFurtherMoves = new JCheckBox("FurtherMoves");
		pnlFooter.add(chckbxFurtherMoves);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		pnlFooter.add(panel);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectListener != null)
					selectListener.selectTicket(null);
			}
		});
		// TODO Shortcut: Escape (per Action?)
		panel.add(btnCancel);
		
		JPanel pnlHeader = new JPanel();
		add(pnlHeader, BorderLayout.NORTH);
		
		lblSelectATicket = new JLabel("Select a Ticket");
		pnlHeader.add(lblSelectATicket);

	}
	
	private void clearTicketCounts() {
		for (int i = 0; i < ticketCounts.length; i++) {
			ticketCounts[i] = 0;
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
		pnlTickets.removeAll();
		for (int i = 0; i < count; i++) {
			Class<? extends Ticket> type = types.get(i);
			JButton btn = new JButton(type.getSimpleName());
//			btn.setIconTextGap(5);
			btn.setVerticalTextPosition(JButton.BOTTOM); // Text unterhalb des Icons (reicht das schon?)
			// TODO lieber ein schoenes Bild anzeigen
			btn.addActionListener(new SelectAction(type));
			pnlTickets.add(btn);
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
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
		lblSelectATicket.setText(String.format("Select a ticket for %s's next move", player));
	}

	public Set<Ticket> getTickets() {
		return tickets;
	}

	public void setTickets(Set<Ticket> tickets) {
		this.tickets = tickets;
		updateTickets();
	}

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
				Component buttons[] = pnlTickets.getComponents();
				((AbstractButton) buttons[i]).setText(String.format("%s (%d)", type.getSimpleName(), ticketCounts[i]));
				buttons[i].setEnabled(ticketCounts[i] > 0);
			}
		}
	}

	public boolean isFutherMovesCheckBoxVisible() {
		return chckbxFurtherMoves.isVisible();
	}
	
	public void setFurtherMovesCheckBoxVisible(boolean visible) {
		chckbxFurtherMoves.setVisible(visible);
	}
	
	public boolean isFurtherMovesSelected() {
		return chckbxFurtherMoves.isSelected();
	}
	
	public void setFurtherMovesSelected(boolean selected) {
		chckbxFurtherMoves.setSelected(selected);
	}
	
	public TicketSelectListener getSelectListener() {
		return selectListener;
	}

	public void setSelectListener(TicketSelectListener selectListener) {
		this.selectListener = selectListener;
	}

	// requestFocusInWindow bei erstem Ticket button, der enabled ist!
	@Override
	public boolean requestFocusInWindow() {
		for (Component c : pnlTickets.getComponents()) {
			if (c.isEnabled())
				return c.requestFocusInWindow();
		}
		return requestFocusInWindow();
	}
}
