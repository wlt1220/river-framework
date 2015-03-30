package org.riverframework.core.org.openntf.domino;

import lotus.domino.NotesThread;

import org.junit.After;
import org.junit.Before;

public class SessionTest  extends org.riverframework.core.AbstractSessionTest {
	@Before
	public void open() {
		NotesThread.sinitThread();
		super.open();
	}

	@After
	public void close() {
		super.close();
		NotesThread.stermThread();
	}
}