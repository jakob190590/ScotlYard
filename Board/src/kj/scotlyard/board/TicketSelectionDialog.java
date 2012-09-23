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

import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.BlackTicket;
import kj.scotlyard.game.model.item.BusTicket;
import kj.scotlyard.game.model.item.TaxiTicket;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.model.item.UndergroundTicket;

/**
 * Dialog, der das TicketSelectionPanel anzeigt.
 * Der Dialog ist "GUI-naeher" als das Panel;
 * er ist auf der Ebene von Board. Deshalb
 * darf er schon mal die Regeln hardcoden.
 * Konkret betrifft das die CheckBox FurhterMoves
 * und den QuickPlay Modus.
 * @author jakob190590
 *
 */
@SuppressWarnings("serial")
public class TicketSelectionDialog extends JDialog {
	
	private Ticket ticket;
	
	private Class<? extends Player> playerType;
	
	private boolean quickPlay;
	
	private TicketSelectionPanel ticketSelectionPanel;

	/**
	 * Create the dialog.
	 * @param w the owner window - must not be <code>null</code>!
	 * @param playerType the type of Player for which 
	 * the dialog should be configured for
	 */
	@SuppressWarnings("unchecked")
	public TicketSelectionDialog(Window w, Class<? extends Player> playerType) {
		super(w);
		this.playerType = playerType;
		
		setModal(true);
		setTitle("Select a Ticket");
		setSize(450, 300);
		
		getContentPane().setLayout(new BorderLayout());
		ticketSelectionPanel = new TicketSelectionPanel();		
		if (playerType != null) {
			if (playerType == MrXPlayer.class) {
				ticketSelectionPanel.setTicketTypes(TaxiTicket.class, BusTicket.class, 
						UndergroundTicket.class, BlackTicket.class);
			} else /* if (playerType == DetectivePlayer.class) */ {
				ticketSelectionPanel.setTicketTypes(TaxiTicket.class, 
						BusTicket.class, UndergroundTicket.class);
			}
		}
		ticketSelectionPanel.setFurtherMovesCheckBoxVisible(false);
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
		if (player.getClass() != playerType) {
			throw new IllegalArgumentException("Specified player do not match playerType.");
		}
		ticketSelectionPanel.setTickets(tickets);
		ticketSelectionPanel.setPlayer(player);
		ticketSelectionPanel.setFurtherMovesSelected(false);
		ticketSelectionPanel.requestFocusInWindow();
		ticket = null;
		
		setLocation(getOwner().getX() + getOwner().getWidth() / 2 - getWidth() / 2, 
				getOwner().getY() + getOwner().getHeight() / 2 - getHeight() / 2);
		setVisible(true); // modal, bis es durch TicketSelectListener geschlossen wird
		return ticket;
	}
	
	public boolean isQuickPlay() {
		return quickPlay;
	}
	
	public void setQuickPlay(boolean flag) {
		quickPlay = flag;
		if (playerType == MrXPlayer.class)
			ticketSelectionPanel.setFurtherMovesCheckBoxVisible(flag);
	}
	
	public boolean isFurtherMovesSelected() {
		return ticketSelectionPanel.isFurtherMovesSelected();
	}
	
	public void setFurtherMovesSelected(boolean selected) {
		ticketSelectionPanel.setFurtherMovesSelected(selected);
	}
	
}
