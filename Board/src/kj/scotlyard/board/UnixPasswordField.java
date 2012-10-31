/*
 * UnixPasswordField -- A unix style password input
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

import java.awt.event.KeyEvent;

import javax.swing.JPasswordField;
import javax.swing.UIManager;

/**
 * This class implements a unix style password input. Similar like this:
 * <p>
 * <tt>read -s -p "Password: " VARIABLE</tt>
 * </p>
 * <tt>-s</tt> silent, no echo; <tt>-p</tt> prompt text, not implemented here of
 * course; into <tt>VARIABLE</tt>, can be retrieved by
 * <code>getPassword()</code>. Neither copy, cut nor paste are supported.
 * 
 * Note that there is <i>no</i> protection mechanism against spy progs which
 * look into the program memory.
 * 
 * @author Jakob Schöttl
 * 
 */
@SuppressWarnings("serial")
public class UnixPasswordField extends JPasswordField {
	
	private StringBuffer password = new StringBuffer();
	
	public UnixPasswordField() { }

	public UnixPasswordField(int columns) {
		super(columns);
	}
	
	private void provideErrorFeedback() {
		UIManager.getLookAndFeel().provideErrorFeedback(this);
	}

	@Override
	public char getEchoChar() {
		return 0;
	}

	/**
	 * Not supported; This method will do NOTHING. The echo character is not
	 * changeable and will always be <tt>0</tt> (on the surface), that is
	 * <i>no</i> echo character is displayed.
	 */
	@Override
	public void setEchoChar(char c) { }

	@Override
	public char[] getPassword() {
		final int l = password.length();
		char[] result = new char[l];
		password.getChars(0, l, result, 0);
		return result;
	}

	@Override
	protected void processKeyEvent(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_TYPED) {
			char c = e.getKeyChar();
			switch (c) {
			case KeyEvent.VK_BACK_SPACE:
				int l = password.length();
				if (l > 0) {
					password.deleteCharAt(l - 1);
				} else {
					// Ein UnixPasswordField soll NICHTS ueber das Passwort verraten
					// Also soll man auch nicht nach Passworteingabe, die Anzahl der
					// eingegebenen Zeichen zaehlen koennen.
					// Ansonsten waere folgendes aber ganz nett:
//					provideErrorFeedback();
				}
				break;
	
			default:
				password.append(e.getKeyChar());
				break;
			}
		} else {
			super.processKeyEvent(e);
		}
	}

	@Override
	public void cut() {
		provideErrorFeedback();
	}

	@Override
	public void copy() {
		provideErrorFeedback();
	}

	@Override
	public void paste() {
		provideErrorFeedback();
	}
	
	/**
	 * Clear the password (like <code>setText("")</code>
	 * for normal text fields).
	 */
	public void clear() {
		password.setLength(0);
	}
	
	/**
	 * Check weather the password is blank.
	 * @return <code>true</code> if the password is empty
	 */
	public boolean isPasswordEmpty() {
		return password.length() == 0;
	}

}
