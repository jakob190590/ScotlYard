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

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import kj.scotlyard.board.metadata.GameMetaData;
import kj.scotlyard.board.metadata.ItemTypeMetaData;
import kj.scotlyard.game.model.DefaultMove;
import kj.scotlyard.game.model.Move;
import kj.scotlyard.game.model.item.Ticket;

/**
 * Stellt einen Move in der History dar.
 * Gedacht ist es fuer die Moves von MrX.
 * Die Variable <code>move</code> kann
 * absichtlich nicht komplett ersetzt werden,
 * weil nur bestimmte Daten des Moves aenderbar
 * sind.
 * Ausserdem muss dieses Panel auch fuer einen
 * Move stehen koennen, der noch nicht
 * ausgefuehrt wurde, um z.B. die MrX
 * uncover positions anzuzeigen.
 * @author jakob190590
 *
 */
@SuppressWarnings("serial")
public class MovePanel extends JPanel {
	
	/**
	 * Wert für Round Number, wenn eine Zuordnung
	 * zu einer Runde (noch) nicht feststeht.
	 */
	public static final int NO_ROUND_NUMBER = -1;
	
	/**
	 * Nur zum speichern einiger Daten des MovePanels.
	 * Ein MovePanel repaesentiert einen Single Move,
	 * deswegen ist das Item immer ein Ticket.
	 */
	private final Move move = new DefaultMove();
	
	private JLabel lblMoveNumber;
	private JLabel lblTicket;

	/**
	 * Create the panel.
	 */
	public MovePanel(int moveNumber) {
		move.setMoveNumber(moveNumber);
		move.setRoundNumber(NO_ROUND_NUMBER);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		lblTicket = new JLabel(); // erst mal kein Text
		lblTicket.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(lblTicket);
		
		lblMoveNumber = new JLabel("Move #");
		lblMoveNumber.setText(String.valueOf(moveNumber));
		lblMoveNumber.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		lblMoveNumber.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(lblMoveNumber);

	}
	
	/**
	 * Zum einfachen setzen der wichtigen Eigenschaften.
	 * Dabei werden nur teilweise Daten des Arguments uebertragen.
	 * <code>null</code> bedeutet Daten loeschen.
	 * @param move
	 */
	public void setMove(Move move) {
		if (move != null) {
			setRoundNumber(move.getRoundNumber());
			setTicket((Ticket) move.getItem());
		} else {
			setRoundNumber(NO_ROUND_NUMBER);
			setTicket(null);
		}
	}
	
	public int getMoveNumber() {
		return move.getMoveNumber();
	}

	public int getRoundNumber() {
		return move.getRoundNumber();
	}

	public void setRoundNumber(int roundNumber) {
		move.setRoundNumber(roundNumber);
	}
	
	public Ticket getTicket() {
		return (Ticket) move.getItem();
	}
	
	public void setTicket(Ticket ticket) {
		move.setItem(ticket);
		if (ticket != null) {
			ItemTypeMetaData itmd = GameMetaData.getForItemType(ticket.getClass());
			lblTicket.setText(itmd.getName());
			lblTicket.setIcon(itmd.getIcon());
		} else {
			lblTicket.setText("");
			lblTicket.setIcon(null);
			// oder visible = false und oben visible = true
		}
	}

}
