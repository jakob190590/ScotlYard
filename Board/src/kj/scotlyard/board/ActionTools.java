package kj.scotlyard.board;

import java.awt.Component;
import java.awt.Container;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.Action;

import org.apache.log4j.Logger;

public abstract class ActionTools {
	
	private static final Logger logger = Logger.getLogger(ActionTools.class);
	
	public static void setSelected(Action action, boolean value) {
		action.putValue(Action.SELECTED_KEY, value);
	}
	
	public static boolean isSelected(Action action) {
		return (boolean) action.getValue(Action.SELECTED_KEY);
	}
	
	/**
	 * Set the values for <code>NAME</code>, <code>MNEMONIC_KEY</code> and
	 * <code>DISPLAYED_MNEMONIC_INDEX_KEY</code> for an Action.
	 * If the first ampersand (&) in <code>name</code> is followed by a letter
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
	
	/**
	 * Automatically assign mnemonics to all Actions of
	 * AbstractButton objects in the specified Container.
	 * If a mnemonic is already defined for an Action,
	 * this mnemonic is preserved under the condition,
	 * it is not yet automatically assigned to an
	 * foregoing Action.
	 * @param container
	 */
	/*
	 * TODO Was vllt mal nett waere:
	 * 
	 * Mnemonics automatisch vergeben
	 * Kriterien dass man sich's gut merken kann:
	 *  - Grosse Anfangsbuchstaben
	 *  - Grosse Buchstaben
	 *  - Anfangsbuchstaben
	 * Schwierigkeit: Darf nicht mehrfach vergeben werden
	 * Moeglichkeit: JMenu uebergeben
	 * JMenu -> Items von oben nach unten durchgehen,
	 * Mnemonic auf black list (bevorzugt von Action nehmen)
	 * 
	 * bereits vergebene mnemonics lassen und zum set adden?
	 * wenn schon im set, neu vergeben?
	 * 
	 */
	public static void assignMnemonicsAutmatically(Container container) {
		Set<Integer> mnemonics = new HashSet<>(); // assigned mnemonics
		for (Component c : container.getComponents()) {
			if (c instanceof AbstractButton) {
				Action a = ((AbstractButton) c).getAction();
				if (a != null) {
					String name = (String) a.getValue(Action.NAME);
					Integer mn = (Integer) a.getValue(Action.MNEMONIC_KEY);
					logger.debug("automatically mnemonic assigning for action " + a);
					if (mn != null && !mnemonics.contains(mn)) {
						// mnemonic schon festgelegt, und noch nicht automatisch
						// woanders zugewiesen: belassen und dem set hinzufuegen
						mnemonics.add(mn);
					} else {
						// erst mal ganz einfach (anfangsbuchstabe verwenden):
						char d;
						if (name != null && name.length() > 0
								&& Character.isLetterOrDigit(d = name.charAt(0))) {
							mn = (int) Character.toUpperCase(d); // mnemonic
							a.putValue(Action.MNEMONIC_KEY, mn);
							logger.debug("mnemonic automatically assigned: " + d);
							mnemonics.add(mn);
						}
					}
				}
			}
		}
	}

}
