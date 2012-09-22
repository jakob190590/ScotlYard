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
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JLabel;

import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.Ticket;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class TicketSelectionPanel extends JPanel {
	
	private Player player;
	private Set<Ticket> tickets;
	private Map<Class<? extends Ticket>, Integer> ticketCounts = new HashMap<>();
	
	private JPanel pnlTickets;
	
	// Der Parameter source des ActionEvents steht fuer das ausgewaehlte Ticket!
	private TicketSelectListener selectListener;
	private JLabel lblSelectATicket;
	
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
		
		JCheckBox chckbxFurtherMoves = new JCheckBox("FurtherMoves");
		pnlFooter.add(chckbxFurtherMoves);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectListener != null)
					selectListener.selectTicket(null);
			}
		});
		pnlFooter.add(btnCancel);
		
		JPanel pnlHeader = new JPanel();
		add(pnlHeader, BorderLayout.NORTH);
		
		lblSelectATicket = new JLabel("Select a Ticket");
		pnlHeader.add(lblSelectATicket);

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
		pnlTickets.removeAll();
		ticketCounts.clear();
		if (tickets != null) {
			// Tickets klassifizieren und zaehlen
			for (Ticket t : tickets) {
				Class<? extends Ticket> key = t.getClass();
				if (ticketCounts.get(key) == null) {
					ticketCounts.put(key, 1);
				} else {
					ticketCounts.put(key, ticketCounts.get(key) + 1);
				}
			}
			// Buttons fuer Klassifizierungen erstellen
			for (Map.Entry<Class<? extends Ticket>, Integer> e : ticketCounts.entrySet()) {
				JButton btn = new JButton();
				btn.setText(String.format("%s (%d)", e.getKey(), e.getValue()));
				btn.setIconTextGap(5);
				btn.setVerticalTextPosition(JButton.BOTTOM); // Text unterhalb des Icons (reicht das schon?)
				// TODO lieber ein schoenes Bild anzeigen, anstatt dem full qualified class name
				btn.addActionListener(new SelectAction(e.getKey()));
				pnlTickets.add(btn);
			}
		}
	}

	public TicketSelectListener getSelectListener() {
		return selectListener;
	}

	public void setSelectListener(TicketSelectListener selectListener) {
		this.selectListener = selectListener;
	}

}
