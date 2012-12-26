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

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class UnixPasswordFieldTestFrame extends JFrame {

	private JPanel contentPane;
	private JPasswordField passwordField;
	private final Action getPasswordAction = new GetPasswordAction();
	private JButton btnSetText;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UnixPasswordFieldTestFrame frame = new UnixPasswordFieldTestFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public UnixPasswordFieldTestFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 429, 92);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		passwordField = new UnixPasswordField();
		passwordField.setColumns(20);
		passwordField.setAction(getPasswordAction);
		contentPane.add(passwordField);
		
		JButton btnGetPassword = new JButton("Get Password");
		btnGetPassword.setAction(getPasswordAction);
		contentPane.add(btnGetPassword);
		
		btnSetText = new JButton("Set Text");
		btnSetText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = "A Password";
				if (JOptionPane.showInputDialog(UnixPasswordFieldTestFrame.this, "Geben Sie ein Passwort ein!", s) != null)
					passwordField.setText(s);
			}
		});
		contentPane.add(btnSetText);
	}

	private class GetPasswordAction extends AbstractAction {
		public GetPasswordAction() {
			super("Get Password");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String s = new String(passwordField.getPassword());
			String msg;
			if (s.equals("your password")) {
				msg = "Your password is correct.";
			} else {
				msg = "Your password is " + (s.isEmpty() ? "empty." : ": \"" + s + "\"");
			}
			JOptionPane.showMessageDialog(UnixPasswordFieldTestFrame.this, msg);
		}
	}
	
}
