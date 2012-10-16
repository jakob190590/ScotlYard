package kj.scotlyard.board.util;

import java.awt.Component;
import java.awt.Container;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * <code>DISPLAYED_MNEMONIC_INDEX_KEY</code> for an <code>Action</code>.
	 * Search for the first ampersand (&) in <code>name</code> followed by a
	 * letter or a digit and than use this letter or digit for the mnemonic. The
	 * ampersand will be deleted in this case. If there is no such ampersand,
	 * only the name is set for the <code>Action</code>.
	 * 
	 * @param action the <code>Action</code> to set name and mnemonic
	 * @param name the name, optional with an ampersand to mark the mnemonic
	 */
	public static void setNameAndMnemonic(Action action, String name) {
		// Erstes & finden, Index merken und entfernen
		int index = findMnemonicAmpersand(name);
		if (index >= 0) {
			StringBuffer s = new StringBuffer(name);
			s.deleteCharAt(index); // & loeschen
			char c = s.charAt(index); // mnemonic char
				
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
			action.putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, index); // einfach immer setzen
		}
		
		action.putValue(Action.NAME, name);
	}
	
	/**
	 * Search for an ampersand (&) followed by a letter or a digit
	 * and return the index of the first occurrence or <code>-1</code>
	 * if there is no such ampersand. Note that umlauts are not letters
	 * in this context.
	 * @param name the action's name
	 * @return the index of the first mnemonic ampersand or <code>-1</code>
	 */
	public static int findMnemonicAmpersand(String name) {
		final Pattern mnemonicAmpersand = Pattern.compile("&[\\w&&[^_]]"); // /&[a-zA-Z0-9]/
		Matcher matcher = mnemonicAmpersand.matcher(name);
		if (matcher.find()) {
			return matcher.start();
		} else {
			return -1;
		}
	}
	
	static class Mnemonic {
		public int mnemonic;
		public int displayedMnemonicIndex;
		public Mnemonic(int mnemonic, int displayedMnemonicIndex) {
			this.mnemonic = mnemonic;
			this.displayedMnemonicIndex = displayedMnemonicIndex;
		}
	}

	/**
	 * Automatically assign mnemonics to all AbstractButtons in the specified
	 * Container. If there is an Action defined for the AbstractButton, the
	 * mnemonic will be set for the Action! If a mnemonic is already defined for
	 * an AbstractButton or Action, this mnemonic is preserved under the
	 * condition, it is not yet automatically assigned to an foregoing
	 * AbstractButton or Action. Otherwise a new mnemonic will be assigned.
	 * 
	 * Note that this method is intended to be invoked with a JMenu as
	 * parameter. Do not pass both a JMenu and a toolbar as parameter, when
	 * there are common Actions.
	 * 
	 * @param container
	 */
	public static void assignMnemonicsAutmatically(Container container) {
		// TODO weitere params fuer:
		// recursive for each Container
		// preserveMnemonics when already assigned
		// excludeAssignedMnemonics exclude already assigned mnemonics from the first
		
		Set<Integer> mnemonics = new HashSet<>(); // assigned mnemonics
		for (Component c : container.getComponents()) {
			if (c instanceof AbstractButton) {
				AbstractButton btn = (AbstractButton) c;
				Action a = btn.getAction();
				String name;
				Integer mn;
				if (a == null) {
					// Keine Action registriert, z.B. bei JMenu
					name = btn.getText();
					mn = btn.getMnemonic();
				} else {
					name = (String) a.getValue(Action.NAME);
					mn = (Integer) a.getValue(Action.MNEMONIC_KEY);
				}
				if (mn != null && mn != 0 && !mnemonics.contains(mn)) {
					// mnemonic schon festgelegt, und noch nicht automatisch
					// woanders zugewiesen: belassen und dem set hinzufuegen
					mnemonics.add(mn);
				} else if (name != null && !name.isEmpty()) { // (... aber wer macht schon eine Action ohne Name)
					// Mnemonic automatisch bestimmen
					Mnemonic mnemonic = getBestMnemonic(name, mnemonics);
					if (mnemonic != null) {
						if (a == null) {
							// Keine Action registriert, z.B. bei JMenu
							btn.setMnemonic(mnemonic.mnemonic);
							btn.setDisplayedMnemonicIndex(mnemonic.displayedMnemonicIndex);
						} else {
							a.putValue(Action.MNEMONIC_KEY, mnemonic.mnemonic);
							a.putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, mnemonic.displayedMnemonicIndex);
						}
						mnemonics.add(mnemonic.mnemonic);
					}
				}
			}
		}
	}
	
	/**
	 * Returns a convenient mnemonic for an action name, or
	 * <code>null</code> if there is no mnemonic possible.
	 * @param name name of an Action or AbstractButton
	 * (<code>getText()</code>)
	 * @param mnemonics a set of mnemonics which are
	 * already assigned and thus not available
	 * @return a convenient mnemonic or <code>null</code>
	 */
	static Mnemonic getBestMnemonic(String name, Set<Integer> mnemonics) {
		
		/*
		 * Mnemonics automatisch vergeben
		 * Kriterien dass man sich's gut merken kann:
		 *  - Allererster Buchstabe
		 *  - Grosse Anfangsbuchstaben
		 *  - Grosse Buchstaben
		 *  - Anfangsbuchstaben
		 *  - alle anderen Buchstaben
		 */
		
		logger.debug("mnemonic searching in: " + name);
		Mnemonic result = null;
		// For Regex/Pattern see: http://docs.oracle.com/javase/1.4.2/docs/api/java/util/regex/Pattern.html
		// final, dass sie nur einmal compiliert werden
		final Pattern firstChar = Pattern.compile("^\\w"); // Allererster Buchstabe (normalerweise ist der ja eh gross, bei z.B. jQuery soll er's sein!)
		final Pattern anyUpperCaseChar = Pattern.compile("\\p{Upper}"); // Irgendein grosser Buchstabe
		final Pattern lowerCaseWord = Pattern.compile("\\b\\p{Lower}\\w*"); // Kleiner Buchstabe am Wortanfang
		final Pattern anyChar = Pattern.compile("\\w"); // Irgendein word char
		final Pattern anyConsonant = Pattern.compile("\\w&&[^aeiou]"); // Konsonant, weil die glaub ich charakteristischer sind fuer ein wort, als vokale
		final Pattern anyVowel = Pattern.compile("[aeiou]"); // Vokale in letzter Instanz
		// TODO Consonant and Vowel noch einsetzen
		final Pattern[] patterns = new Pattern[] { firstChar,
				anyUpperCaseChar, lowerCaseWord, anyChar };
				
		for (Pattern p : patterns) {
			logger.debug("mnemonic searching with: " + p);
			Matcher matcher = p.matcher(name);
			while (matcher.find()) {
				String s = matcher.group();
				char d = s.charAt(0);
				logger.debug("checking mnemonic char: " + d);
				int mn = Character.toUpperCase(d);
				if (!mnemonics.contains(mn)) {
					logger.info("mnemonic automatically assigned: " + d + " at " + matcher.start());
					result = new Mnemonic(mn, matcher.start());
					break;
				}
			}
			if (result != null) {
				break;
			}
		}
		return result;
	}

}
