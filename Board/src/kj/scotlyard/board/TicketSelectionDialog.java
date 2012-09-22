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

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.Set;
import javax.swing.JDialog;

import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.Ticket;

@SuppressWarnings("serial")
public class TicketSelectionDialog extends JDialog {
	
	private Ticket ticket;
	
	private TicketSelectionPanel ticketSelectionPanel;

	/**
	 * Create the dialog.
	 */
	public TicketSelectionDialog(Window w) {
		super(w);
		setModal(true);
		setTitle("Select a Ticket");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		ticketSelectionPanel = new TicketSelectionPanel();
		ticketSelectionPanel.setSelectListener(new TicketSelectListener() {
			@Override
			public void selectTicket(Ticket ticket) {
				TicketSelectionDialog.this.ticket = ticket;
				setVisible(false);
			}
		});
				
		getContentPane().add(ticketSelectionPanel, BorderLayout.CENTER);
	}

	public Ticket show(Set<Ticket> tickets, Player player) {
		ticketSelectionPanel.setTickets(tickets);
		ticketSelectionPanel.setPlayer(player);
		ticket = null;
		setVisible(true); // modal, bis es durch TicketSelectListener geschlossen wird
		return ticket;
	}
	
}
