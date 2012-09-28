package kj.scotlyard.board;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import kj.scotlyard.board.metadata.GameMetaData;
import kj.scotlyard.game.model.DetectivePlayer;
import kj.scotlyard.game.model.Player;

@SuppressWarnings("serial")
public class PlayerComboBoxRenderer extends JLabel implements
		ListCellRenderer<Player> {

	public PlayerComboBoxRenderer() {
		setOpaque(true);
		setVerticalAlignment(CENTER);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Player> list,
			Player value, int index, boolean isSelected, boolean cellHasFocus) {

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		if (value != null) {
			// TODO evtl farbe je nach player unterschiedlich
			setFont(list.getFont());
			setText(GameMetaData.getForPlayer(value).getName()
					+ ((value instanceof DetectivePlayer) ?
								(" - " + value.hashCode()) : ""));
		}

		return this;
	}

}
