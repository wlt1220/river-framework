package org.riverframework.domino;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import lotus.domino.NotesThread;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.riverframework.RiverException;
import org.riverframework.domino.Database;
import org.riverframework.domino.DefaultDatabase;
import org.riverframework.domino.DefaultDocument;
import org.riverframework.domino.DefaultSession;
import org.riverframework.domino.DefaultView;
import org.riverframework.domino.Document;
import org.riverframework.domino.Session;

public class ViewTest {
	final String TEST_FORM = "TestForm";
	final String TEST_VIEW = "TestView";

	final Session session = DefaultSession.getInstance();
	private Database rDatabase = null;

	@Before
	public void init() {
		NotesThread.sinitThread();

		try {
			session.open(LocalContext.getPassword());
			rDatabase = session.getDatabase(DefaultDatabase.class, "", LocalContext.getDatabase());

		} catch (Exception e) {
			throw new RiverException(e);
		}
	}

	@After
	public void close() {
		session.close();
		NotesThread.stermThread();
	}

	@Test
	public void testGetDocumentAndRefreshView() throws InterruptedException {
		assertTrue("The test database could not be opened.", rDatabase.isOpen());

		RandomString rs = new RandomString(10);
		DefaultView rView = rDatabase.getView(DefaultView.class, TEST_VIEW);

		assertTrue("The test view could not be created in the test database.", rView.isOpen());

		Document rDoc = rDatabase.createDocument(DefaultDocument.class)
				.setForm(TEST_FORM);

		String key = rs.nextString();

		rDoc.setField("TestKeyColumn1", key)
				.save();
		rView.refresh();

		rDoc = null;
		rDoc = rView.getDocumentByKey(DefaultDocument.class, key);
		assertTrue("The test document could not be found in the view.", rDoc.isOpen());

		rDoc = null;
		rDoc = rView.getDocumentByKey(DefaultDocument.class, "%%%%%");
		assertFalse("It was found an unexistent document in the view.", rDoc.isOpen());

		/*
		 * This code doesn't works because the created view does not works
		 * as expected. It's necessary to open the Designer and update the
		 * view to get it works.
		 * 
		 * String viewName = rs.nextString();
		 * String formName = rs.nextString();
		 * org.riverframework.View rView = rDatabase.getView(viewName);
		 * if(!rView.isOpen()) {
		 * rView = rDatabase.createView(viewName, "Form =\"" + formName + "\"");
		 * }
		 * 
		 * assertTrue("The test view could not be created in the test database.", rView.isOpen());
		 * 
		 * String title = rs.nextString();
		 * String field = rs.nextString();
		 * String formula = "@GetField({" + field + "})";
		 * String key = rs.nextString();
		 * 
		 * rView.addColumn(0).modifyColumn(0, title, formula, true).refresh();
		 * org.riverframework.Document rDoc = rDatabase.createDocument(formName);
		 * 
		 * rDoc.setField(field, key);
		 * rDoc.save();
		 */
	}

	@Test
	public void testIsOpen() {
		assertTrue("The test database could not be opened.", rDatabase.isOpen());

		// RandomString rs = new RandomString(10);
		DefaultView rView = rDatabase.getView(DefaultView.class, TEST_VIEW);

		assertTrue("The test view could not be found in the test database.", rView.isOpen());

		rView = null;
		rView = rDatabase.getView(DefaultView.class, "%%%$%$%$%%$");

		assertFalse("An unexistant view could be found in the test database.", rView.isOpen());
	}

	/*
	 * @Test
	 * public void testModifyColumn() {
	 * assertTrue("The test database could not be opened.", rDatabase.isOpen());
	 * 
	 * RandomString rs = new RandomString(10);
	 * org.riverframework.View rView = rDatabase.createView(rs.nextString(), "Select @All");
	 * 
	 * assertTrue("The test view could not be created in the test database.", rView.isOpen());
	 * 
	 * String title = rs.nextString();
	 * String formula = rs.nextString();
	 * 
	 * rView.modifyColumn(0, title, formula, true);
	 * 
	 * assertTrue("Could not be modified the title in the first column of the test view.",
	 * title.equals(rView.getColumnTitle(0)));
	 * assertTrue("Could not be modified the formula in the first column of the test view.",
	 * formula.equals(rView.getColumnFormula(0)));
	 * assertTrue("Could not be modified if it is sorted in the first column of the test view.",
	 * rView.isColumnSorted(0));
	 * }
	 */

	@Test
	public void testIteration() {
		assertTrue("The test database could not be opened.", rDatabase.isOpen());

		DefaultDocument rDoc = (DefaultDocument) rDatabase
				.createDocument(DefaultDocument.class)
				.setForm(TEST_FORM)
				.save();

		DefaultView rView = rDatabase.getView(DefaultView.class, TEST_VIEW);

		rDoc = null;

		while (rView.hasNext()) {
			rDoc = (DefaultDocument) rView.next();
		}

		assertTrue("There is a problem getting documents from the database.", rDoc != null && rDoc.isOpen());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testRemove() {
		assertTrue("The test database could not be opened.", rDatabase.isOpen());

		DefaultView rView = rDatabase.getView(DefaultView.class, TEST_VIEW);
		rView.remove();
	}
}