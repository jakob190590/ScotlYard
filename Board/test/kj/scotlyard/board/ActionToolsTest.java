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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.junit.Before;
import org.junit.Test;

public class ActionToolsTest {
	
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

}
