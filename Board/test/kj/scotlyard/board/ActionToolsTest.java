package kj.scotlyard.board;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_E;
import static java.awt.event.KeyEvent.VK_L;
import static java.awt.event.KeyEvent.VK_M;
import static java.awt.event.KeyEvent.VK_R;
import static java.awt.event.KeyEvent.VK_X;
import static javax.swing.Action.DISPLAYED_MNEMONIC_INDEX_KEY;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.NAME;
import static kj.scotlyard.board.ActionTools.setNameAndMnemonic;
import static org.junit.Assert.assertEquals;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;

public class ActionToolsTest {
	
	static { BasicConfigurator.configure(); }
	
	@SuppressWarnings("serial")
	class MyTestAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) { }
	};

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public final void testSetNameAndMnemonic() {
		Action a;
		String name;
		
		a = new MyTestAction();
		name = "MrX Always Visible";
		setNameAndMnemonic(a, name);
		assertEquals("MrX Always Visible", a.getValue(NAME));
		assertEquals(null, a.getValue(MNEMONIC_KEY));
		assertEquals(null, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "&MrX& Alway&s &Visible";
		setNameAndMnemonic(a, name);
		assertEquals("MrX& Alway&s &Visible", a.getValue(NAME));
		assertEquals(VK_M, a.getValue(MNEMONIC_KEY));
		assertEquals(null, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "M&rX Always Visible";
		setNameAndMnemonic(a, name);
		assertEquals("MrX Always Visible", a.getValue(NAME));
		assertEquals(VK_R, a.getValue(MNEMONIC_KEY));
		assertEquals(null, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "&MrX Always Visible";
		setNameAndMnemonic(a, name);
		assertEquals("MrX Always Visible", a.getValue(NAME));
		assertEquals(VK_M, a.getValue(MNEMONIC_KEY));
		assertEquals(null, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "Mr&X Always Visible";
		setNameAndMnemonic(a, name);
		assertEquals("MrX Always Visible", a.getValue(NAME));
		assertEquals(VK_X, a.getValue(MNEMONIC_KEY));
		assertEquals(null, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "MrX& &Always Visible";
		setNameAndMnemonic(a, name);
		assertEquals("MrX& &Always Visible", a.getValue(NAME));
		assertEquals(null, a.getValue(MNEMONIC_KEY));
		assertEquals(null, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "MrX &Always Visible";
		setNameAndMnemonic(a, name);
		assertEquals("MrX Always Visible", a.getValue(NAME));
		assertEquals(VK_A, a.getValue(MNEMONIC_KEY));
		assertEquals(null, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "MrX Alw&ays Visible";
		setNameAndMnemonic(a, name);
		assertEquals("MrX Always Visible", a.getValue(NAME));
		assertEquals(VK_A, a.getValue(MNEMONIC_KEY));
		assertEquals(7, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "MrX Always Visibl&e";
		setNameAndMnemonic(a, name);
		assertEquals("MrX Always Visible", a.getValue(NAME));
		assertEquals(VK_E, a.getValue(MNEMONIC_KEY));
		assertEquals(null, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "MrX Always Visib&le";
		setNameAndMnemonic(a, name);
		assertEquals("MrX Always Visible", a.getValue(NAME));
		assertEquals(VK_L, a.getValue(MNEMONIC_KEY));
		assertEquals(16, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "MrX Always Visible&";
		setNameAndMnemonic(a, name);
		assertEquals("MrX Always Visible&", a.getValue(NAME));
		assertEquals(null, a.getValue(MNEMONIC_KEY));
		assertEquals(null, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "MrX Always&& Visible&";
		setNameAndMnemonic(a, name);
		assertEquals("MrX Always&& Visible&", a.getValue(NAME));
		assertEquals(null, a.getValue(MNEMONIC_KEY));
		assertEquals(null, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "MrX &Älways&& Visible&";
		setNameAndMnemonic(a, name);
		assertEquals("MrX Älways&& Visible&", a.getValue(NAME));
		assertEquals((int) 'Ä', a.getValue(MNEMONIC_KEY));
		assertEquals(null, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "MrX &0lways&& Visible&";
		setNameAndMnemonic(a, name);
		assertEquals("MrX 0lways&& Visible&", a.getValue(NAME));
		assertEquals((int) '0', a.getValue(MNEMONIC_KEY));
		assertEquals(null, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "MrX &1lways&& Visible&";
		setNameAndMnemonic(a, name);
		assertEquals("MrX 1lways&& Visible&", a.getValue(NAME));
		assertEquals((int) '1', a.getValue(MNEMONIC_KEY));
		assertEquals(null, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
		a = new MyTestAction();
		name = "MrX &9lways&& Visible&";
		setNameAndMnemonic(a, name);
		assertEquals("MrX 9lways&& Visible&", a.getValue(NAME));
		assertEquals((int) '9', a.getValue(MNEMONIC_KEY));
		assertEquals(null, a.getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		
	}
	
	@SuppressWarnings("serial")
	@Test
	public final void testAssignMnemonicsAutmatically() {
		Container c;
		Action[] as;
		
		// alle actions ohne namen
		c = new JPanel();
		as = new Action[10];
		for (int i = 0; i < as.length; i++) {
			Action a = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) { }
			};
			c.add(new JButton(a));
			as[i] = a;
		}
		ActionTools.assignMnemonicsAutmatically(c);
		for (Action a : as) {
			assertEquals(null, a.getValue(NAME));
			assertEquals(null, a.getValue(MNEMONIC_KEY));
		}

		// paar actions ohne namen
		c = new JPanel();
		as = new Action[10];
		for (int i = 0; i < as.length; i++) {
			Action a = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) { }
			};
			c.add(new JButton(a));
			as[i] = a;
		}
		as[0].putValue(NAME, "abc");
		as[2].putValue(NAME, "bcd");
		as[5].putValue(NAME, "cde");
		as[6].putValue(NAME, "def");
		ActionTools.assignMnemonicsAutmatically(c);
		assertEquals((int) 'A', as[0].getValue(MNEMONIC_KEY));
		assertEquals(null, as[1].getValue(MNEMONIC_KEY));
		assertEquals((int) 'B', as[2].getValue(MNEMONIC_KEY));
		assertEquals(null, as[3].getValue(MNEMONIC_KEY));
		assertEquals(null, as[4].getValue(MNEMONIC_KEY));
		assertEquals((int) 'C', as[5].getValue(MNEMONIC_KEY));
		assertEquals((int) 'D', as[6].getValue(MNEMONIC_KEY));

		// paar actions mit mnemonic
		c = new JPanel();
		as = new Action[10];
		for (int i = 0; i < as.length; i++) {
			Action a = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) { }
			};
			c.add(new JButton(a));
			as[i] = a;
		}
		as[0].putValue(NAME, "abc");
		ActionTools.setNameAndMnemonic(as[1], "d&ef");
		as[2].putValue(NAME, "bcd");
		as[5].putValue(NAME, "cde");
		as[6].putValue(NAME, "def");
		ActionTools.assignMnemonicsAutmatically(c);
		assertEquals((int) 'A', as[0].getValue(MNEMONIC_KEY));
		assertEquals((int) 'E', as[1].getValue(MNEMONIC_KEY));
		assertEquals((int) 'B', as[2].getValue(MNEMONIC_KEY));
		assertEquals(null, as[3].getValue(MNEMONIC_KEY));
		assertEquals(null, as[4].getValue(MNEMONIC_KEY));
		assertEquals((int) 'C', as[5].getValue(MNEMONIC_KEY));
		assertEquals((int) 'D', as[6].getValue(MNEMONIC_KEY));
		
		// paar actions mit mnemonic
		c = new JPanel();
		as = new Action[10];
		for (int i = 0; i < as.length; i++) {
			Action a = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) { }
			};
			c.add(new JButton(a));
			as[i] = a;
		}
		as[0].putValue(NAME, "abc Aldi"); // a soll's sein
		ActionTools.setNameAndMnemonic(as[1], "ed&ef"); // e soll's sein
		as[2].putValue(NAME, "bcd winkip Plumpsi"); // b soll's sein
		as[3].putValue(NAME, "Ammi Becku");
		as[4].putValue(NAME, "Ammi Becku");
		as[5].putValue(NAME, "Cde dicki"); // C soll's sein
		as[6].putValue(NAME, "defFuff");
		ActionTools.assignMnemonicsAutmatically(c);
		assertEquals((int) 'A', as[0].getValue(MNEMONIC_KEY));
		assertEquals(0, as[0].getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		assertEquals((int) 'E', as[1].getValue(MNEMONIC_KEY));
		assertEquals(2, as[1].getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		assertEquals((int) 'B', as[2].getValue(MNEMONIC_KEY));
		assertEquals(0, as[2].getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		assertEquals((int) 'M', as[3].getValue(MNEMONIC_KEY));
		assertEquals(1, as[3].getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		assertEquals((int) 'I', as[4].getValue(MNEMONIC_KEY));
		assertEquals(3, as[4].getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		assertEquals((int) 'C', as[5].getValue(MNEMONIC_KEY));
		assertEquals(0, as[5].getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
		assertEquals((int) 'D', as[6].getValue(MNEMONIC_KEY));
		assertEquals(0, as[6].getValue(DISPLAYED_MNEMONIC_INDEX_KEY));
	}
	
	@Test
	public final void testGetBestMnemonic() {
		final String TEST_NAME_1 = "jingles JGraphT Select user Jam";
		final String TEST_NAME_2 = "jQuery thing Jakob";
		
		Set<Integer> mnemonics = new HashSet<>();
		ActionTools.Mnemonic mn;
		
		// TEST_NAME_1 "jingles JGraphT Select user Jam"
		
		// grosse anfangsbuchstaben
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('J', mn.mnemonic);
		assertEquals(0, mn.displayedMnemonicIndex);
		
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('S', mn.mnemonic);
		assertEquals(16, mn.displayedMnemonicIndex);
		
		// grosse buchstaben allgemein
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('G', mn.mnemonic);
		assertEquals(9, mn.displayedMnemonicIndex);
		
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('T', mn.mnemonic);
		assertEquals(14, mn.displayedMnemonicIndex);
		
		// kleine anfangsbuchstaben (j schon vergeben)
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('U', mn.mnemonic);
		assertEquals(23, mn.displayedMnemonicIndex);
		
		// alle buchstaben
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('I', mn.mnemonic);
		assertEquals(1, mn.displayedMnemonicIndex);
		
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('N', mn.mnemonic);
		assertEquals(2, mn.displayedMnemonicIndex);
		
		// g entfaellt
		
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('L', mn.mnemonic);
		assertEquals(4, mn.displayedMnemonicIndex);
		
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('E', mn.mnemonic);
		assertEquals(5, mn.displayedMnemonicIndex);
		
		// s faellt weg
		
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('R', mn.mnemonic);
		assertEquals(10, mn.displayedMnemonicIndex);
		
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('A', mn.mnemonic);
		assertEquals(11, mn.displayedMnemonicIndex);
		
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('P', mn.mnemonic);
		assertEquals(12, mn.displayedMnemonicIndex);
		
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('H', mn.mnemonic);
		assertEquals(13, mn.displayedMnemonicIndex);
		
		// T und S entfallen
		
		// e, l, e entfallen
		
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('C', mn.mnemonic);
		assertEquals(20, mn.displayedMnemonicIndex);
		
		// t entfaellt
		// u entfaellt
		
		// s, e, r entfallen
		// J, a entfaellt
		
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('M', mn.mnemonic);
		assertEquals(30, mn.displayedMnemonicIndex);
		
		mn = ActionTools.getBestMnemonic(TEST_NAME_1, mnemonics);
		assertEquals(null, mn);
		
		
		
		
		// TEST_NAME_2  "jQuery thing Jakob"
		mnemonics.clear();
		
		// grosse anfangsbuchstaben
		mn = ActionTools.getBestMnemonic(TEST_NAME_2, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('J', mn.mnemonic);
		assertEquals(0, mn.displayedMnemonicIndex);
		
		// grosse buchstaben allgemein
		mn = ActionTools.getBestMnemonic(TEST_NAME_2, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('Q', mn.mnemonic);
		assertEquals(1, mn.displayedMnemonicIndex);
		
		// kleine anfangsbuchstaben (j schon vergeben)
		mn = ActionTools.getBestMnemonic(TEST_NAME_2, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('T', mn.mnemonic);
		assertEquals(7, mn.displayedMnemonicIndex);
		
		// alle buchstaben
		mn = ActionTools.getBestMnemonic(TEST_NAME_2, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('U', mn.mnemonic);
		assertEquals(2, mn.displayedMnemonicIndex);
	
		mn = ActionTools.getBestMnemonic(TEST_NAME_2, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('E', mn.mnemonic);
		assertEquals(3, mn.displayedMnemonicIndex);
		
		mn = ActionTools.getBestMnemonic(TEST_NAME_2, mnemonics);
		mnemonics.add(mn.mnemonic);
		assertEquals('R', mn.mnemonic);
		assertEquals(4, mn.displayedMnemonicIndex);
	}
}
