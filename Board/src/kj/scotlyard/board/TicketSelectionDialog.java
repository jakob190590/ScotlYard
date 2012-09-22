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
