package kj.scotlyard.board;

import javax.swing.Action;

public abstract class ActionTools {
	
	public static void setSelected(Action action, boolean value) {
		action.putValue(Action.SELECTED_KEY, value);
	}
	
	public static boolean isSelected(Action action) {
		return (boolean) action.getValue(Action.SELECTED_KEY);
	}
	
	/**
	 * Set the values for <code>NAME</code>, <code>MNEMONIC_KEY</code> and
	 * <code>DISPLAYED_MNEMONIC_INDEX_KEY</code> for an Action.
	 * If the first ampersend (&) in <code>name</code> is followed by a letter
	 * or a digit (<code>Character.isLetterOrDigit(char)</code>), this letter
	 * or digit will be the displayed mnemonic key. In this case, the ampersand
	 * will be deleted.
	 * @param action
	 * @param name
	 */
	public static void setNameAndMnemonic(Action action, String name) {
		// Erstes & finden, Index merken und entfernen
		int index = name.indexOf('&');
		if (index >= 0 && index < (name.length() - 1)) {
			StringBuffer s = new StringBuffer(name);
			s.deleteCharAt(index); // & loeschen
			char c = s.charAt(index); // mnemonic char
			if (Character.isLetterOrDigit(c)) {
				
				name = s.toString();
			
//				int keyCode = KeyStroke.getKeyStroke("typed " + Character.toLowerCase(c)).getKeyCode(); // geht wohl nicht
//				int keyCode = KeyStroke.getKeyStroke("typed " + Character.toUpperCase(c)).getKeyCode(); // geht wohl nicht
//				int keyCode = KeyStroke.getKeyStroke(Character.toLowerCase(c)).getKeyCode(); // geht wohl nicht
//				int keyCode = KeyStroke.getKeyStroke(Character.toUpperCase(c)).getKeyCode(); // geht wohl nicht
//				int keyCode = KeyStroke.getKeyStroke(String.valueOf(Character.toLowerCase(c))).getKeyCode(); // null pointer exc
//				int keyCode = KeyStroke.getKeyStroke(String.valueOf(Character.toUpperCase(c))).getKeyCode(); // null pointer exc
//				int keyCode = KeyStroke.getKeyStroke("typed " + c).getKeyCode(); // geht wohl nicht
				
				c = Character.toUpperCase(c);
				action.putValue(Action.MNEMONIC_KEY, (int) c);
				
				if (name.toUpperCase().indexOf(c) < index)
					action.putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, index);
			
			}
		}
		
		action.putValue(Action.NAME, name);
	}

}
