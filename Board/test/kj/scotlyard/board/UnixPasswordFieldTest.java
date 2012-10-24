package kj.scotlyard.board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class UnixPasswordFieldTest {
	UnixPasswordField upf;
	@Before
	public void setUp() throws Exception {
		upf = new UnixPasswordField();
	}

	@Test
	public final void testGetEchoChar() {
		assertEquals(0, upf.getEchoChar());
	}

	@Test
	public final void testSetEchoChar() {
		upf.setEchoChar('a');
		assertEquals(0, upf.getEchoChar());
	}

	@Test
	public final void testGetPassword() {
		// leider scheint dispatchEvent nicht zu funktionieren (vllt, weil komponente nicht angezeigt wird)
//		upf.dispatchEvent(new KeyEvent(upf, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, 0, 'A'));
		String pw = new String(upf.getPassword());
		assertEquals("", pw);
	}

	@Test
	public final void testClear() {
		// leider scheint dispatchEvent nicht zu funktionieren (vllt, weil komponente nicht angezeigt wird)
//		upf.dispatchEvent(new KeyEvent(upf, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, 0, 'A'));
//		assertFalse(upf.isPasswordEmpty());
		upf.clear();
		assertTrue(upf.isPasswordEmpty());
	}

	@Test
	public final void testIsPasswordEmpty() {
		assertTrue(upf.isPasswordEmpty());
		// leider scheint dispatchEvent nicht zu funktionieren (vllt, weil komponente nicht angezeigt wird)
//		upf.dispatchEvent(new KeyEvent(upf, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, 0, 'A'));
//		assertFalse(upf.isPasswordEmpty());
//		upf.dispatchEvent(new KeyEvent(upf, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, 0, (char) 8));
//		assertTrue(upf.isPasswordEmpty());
	}

}
