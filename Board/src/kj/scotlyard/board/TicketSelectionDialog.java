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
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import kj.scotlyard.game.model.MrXPlayer;
import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.BlackTicket;
import kj.scotlyard.game.model.item.BusTicket;
import kj.scotlyard.game.model.item.Item;
import kj.scotlyard.game.model.item.TaxiTicket;
import kj.scotlyard.game.model.item.Ticket;
import kj.scotlyard.game.model.item.UndergroundTicket;
import java.awt.Font;
import javax.swing.AbstractAction;
import javax.swing.Action;

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
	
	private Player player;
	
	private Ticket ticket;
	
	private Class<? extends Player> playerType;
	
	private boolean quickPlay;
	
	private ItemSelectionPanel ticketSelectionPanel;
	
	private JLabel lblSelectATicket;
	private JCheckBox chckbxFurtherMoves;
	private JButton btnCancel;
	private JPanel leftPanel;
	private final Action cancelAction = new CancelAction();

	/**
	 * Create the dialog.
	 * Both params must not be <code>null</code>!
	 * @param w the owner window
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
		
		// Cancel Action auf Escape legen (was bewirkt ACCELERATOR_KEY in Action dann eigentlich?)
		getRootPane().getActionMap().put("cancel", cancelAction);
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
//		                                      ^ optionaler param; aber default = WHEN_FOCUSED!
		
		getContentPane().setLayout(new BorderLayout());
		
		
		
		JPanel pnlFooter = new JPanel();
		getContentPane().add(pnlFooter, BorderLayout.SOUTH);
		pnlFooter.setLayout(new BoxLayout(pnlFooter, BoxLayout.X_AXIS));
		
		leftPanel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) leftPanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		pnlFooter.add(leftPanel);
		
		chckbxFurtherMoves = new JCheckBox("Further Move(s)");
		chckbxFurtherMoves.setMnemonic(KeyEvent.VK_F);
		leftPanel.add(chckbxFurtherMoves);
		chckbxFurtherMoves.setToolTipText("To prepare further move(s) for a multi move");
		
		JPanel rightPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) rightPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		pnlFooter.add(rightPanel);
		
		btnCancel = new JButton("Cancel");
		btnCancel.setAction(cancelAction);
		// cancelAction ist oben schon auf Escape gelegt.
		rightPanel.add(btnCancel);
		
		JPanel pnlHeader = new JPanel();
		getContentPane().add(pnlHeader, BorderLayout.NORTH);
		
		lblSelectATicket = new JLabel("Select a Ticket");
		lblSelectATicket.setFont(new Font("Tahoma", Font.BOLD, 14));
		pnlHeader.add(lblSelectATicket);
		
		
		ticketSelectionPanel = new ItemSelectionPanel();		
		if (playerType == MrXPlayer.class) {
			// alle moeglichen Ticket types
			ticketSelectionPanel.setItemTypes(TaxiTicket.class, BusTicket.class, 
					UndergroundTicket.class, BlackTicket.class);
		} else /* if (playerType == DetectivePlayer.class) */ {
			ticketSelectionPanel.setItemTypes(TaxiTicket.class, 
					BusTicket.class, UndergroundTicket.class);
		}
		ticketSelectionPanel.setSelectListener(new ItemSelectListener() {
			@Override
			public void selectItem(Item ticket) {
				TicketSelectionDialog.this.ticket = (Ticket) ticket;
				setVisible(false);
			}
		});
				
		getContentPane().add(ticketSelectionPanel, BorderLayout.CENTER);
		
		pack();
	}
	
	public Ticket show(Set<Ticket> tickets, Player player) {
		if (player.getClass() != playerType) {
			throw new IllegalArgumentException("Specified player do not match playerType.");
		}
		
		// Noetiger Umweg fuer ein more reuseable ItemSelectionPanel...
		Set<Item> items = new HashSet<>();
		items.addAll(tickets);
		ticketSelectionPanel.setItems(items);
		ticketSelectionPanel.requestFocusInWindow();
		ticket = null;
		
		setLocation(getOwner().getX() + getOwner().getWidth() / 2 - getWidth() / 2, 
				getOwner().getY() + getOwner().getHeight() / 2 - getHeight() / 2);
		setVisible(true); // modal, bis es durch TicketSelectListener oder so geschlossen wird
		return ticket;
	}
	
	public boolean isQuickPlay() {
		return quickPlay;
	}
	
	public void setQuickPlay(boolean flag) {
		quickPlay = flag;
		if (playerType == MrXPlayer.class)
			setFurtherMovesCheckBoxVisible(flag);
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
		lblSelectATicket.setText(String.format("Select a ticket for %s's next move", player));
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
	
	private class CancelAction extends AbstractAction {
		public CancelAction() {
			putValue(NAME, "Cancel");
			putValue(SHORT_DESCRIPTION, "Cancel the ticket selection");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)); // hat offenbar keine Wirkung, deshalb wird ActionMap und InputMap verwendet
			putValue(MNEMONIC_KEY, KeyEvent.VK_C);
		}
		public void actionPerformed(ActionEvent e) {
			ticket = null;
			setVisible(false);
		}
	}
}
