package kj.scotlyard.board;

import javax.swing.Action;
import javax.swing.KeyStroke;

public abstract class ActionTools {
	
	public static void setSelected(Action action, boolean value) {
		action.putValue(Action.SELECTED_KEY, value);
	}
	
	public static boolean isSelected(Action action) {
		return (boolean) action.getValue(Action.SELECTED_KEY);
	}
	
	/**
	 * Set the <code>NAME</code>, <code>MNEMONIC_KEY</code> and
	 * - if necessary - <code>DISPLAYED_MNEMONIC_INDEX_KEY</code>
	 * for an Action. If the name contains one or more ampersands
	 * (&), the letter directly following the first ampersand
	 * will be the mnemonic key. The first ampersand in name will
	 * be removed.
	 * @param action
	 * @param name
	 */
	public static void setNameAndMnemonic(Action action, String name) {
		// Erstes & finden, Index merken und entfernen
		int index = name.indexOf('&');
		if (index >= 0) {
			StringBuffer s = new StringBuffer(name);
			s.deleteCharAt(index);
//			char c = Character.toLowerCase(s.charAt(index)); // mnemonic char
			char c = s.charAt(index); // mnemonic char
			System.out.println(c + " = " + KeyStroke.getKeyStroke("typed " + c).getKeyCode());
			action.putValue(Action.NAME, s.toString());
			action.putValue(Action.MNEMONIC_KEY, KeyStroke.getKeyStroke("typed " + c).getKeyCode());
			if (name.indexOf(c) < index)
				action.putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, index);
			
		} else {
			action.putValue(Action.NAME, name);
		}
	}

}
