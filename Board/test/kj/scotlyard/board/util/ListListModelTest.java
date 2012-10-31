package kj.scotlyard.board.util;

import static org.junit.Assert.*;

import javax.swing.*;

import org.junit.*;

public class ListListModelTest {

	JList<String> jList;
	ListListModel<String> llm;
	
	@Before
	public void setUp() throws Exception {
		llm = new ListListModel<>();
		jList = new JList<>(llm);
	}

	@Test
	public final void testSize() {
		assertEquals(0, llm.size());
		llm.add("");
		assertEquals(1, llm.size());
		llm.remove(0);
		assertEquals(0, llm.size());
	}

	@Test
	public final void testGetInt() {
		llm.add("asdf");
		assertEquals("asdf", llm.get(0));
	}

	@Test
	public final void testGetElementAt() {
		llm.add("asdf");
		assertEquals("asdf", llm.getElementAt(0));
	}

	@Test
	public final void testGetSize() {
		assertEquals(0, jList.getModel().getSize());
		llm.add("");
		assertEquals(1, jList.getModel().getSize());
		llm.remove(0);
		assertEquals(0, jList.getModel().getSize());
	}

	@Test
	public final void testSetIntE() {
		llm.add("asdf");
		llm.set(0, "jkl;");
		assertEquals("jkl;", llm.getElementAt(0));
	}

	@Test
	public final void testAddIntE() {
		llm.add("asdf");
		llm.add(0, "jkl;");
		assertEquals("jkl;", llm.getElementAt(0));
	}

	@Test
	public final void testRemoveInt() {
		llm.add("asdf");
		llm.remove(0);
		assertEquals(0, llm.size());
	}

}
