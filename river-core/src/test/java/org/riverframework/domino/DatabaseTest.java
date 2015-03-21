package org.riverframework.domino;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import lotus.domino.NotesThread;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.riverframework.RiverException;

public class DatabaseTest {
	final String TEST_FORM = "TestForm";
	final String TEST_VIEW = "TestView";
	final String TEST_GRAPH = "TestGraph";

	private Session session = DefaultSession.getInstance();
	private DefaultDatabase rDatabase = null;
	private VacationDatabase rVacationDatabase = null;

	@Before
	public void init() {
		NotesThread.sinitThread();

		try {
			session.open(Credentials.getPassword());
			rDatabase = session.getDatabase(DefaultDatabase.class, "", Context.getDatabase());
			rVacationDatabase = session.getDatabase(VacationDatabase.class, "", Context.getDatabase());

			rDatabase.getAllDocuments().removeAll();
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
	public void testOpenedDatabase() {

		assertTrue("The test database could not be opened.", rDatabase.isOpen());
		assertFalse("The file path could not be detected.", rDatabase.getFilePath().equals(""));
		assertFalse("The database name could not be detected.", rDatabase.getName().equals(""));
	}

	// @Test
	// public void testCreateAndGetGraph() {
	// assertTrue("The test database could not be opened.", rDatabase.isOpen());
	//
	// org.riverframework.development.Relation rRelation = rDatabase.createRelation(TEST_GRAPH);
	//
	// assertTrue("There is a problem creating a new graph in the test database.", rRelation.isOpen());
	//
	// String universalId = rRelation.save(org.riverframework.Document.FORCE_SAVE).getUniversalId();
	// rRelation = null;
	//
	// rRelation = rDatabase.getRelation(universalId);
	//
	// assertTrue("There is a problem getting the graph created in the test database.",
	// rRelation.isOpen());
	// }

	@Test
	public void testGetView() {
		assertTrue("The test database could not be instantiated.", rDatabase != null);
		assertTrue("The test database could not be opened.", rDatabase.isOpen());

		DefaultView rView = rDatabase.getView(DefaultView.class, TEST_VIEW);

		assertTrue("There is a problem getting the view created in the test database.", rView.isOpen());
	}

	static class VacationDatabase extends org.riverframework.domino.DefaultDatabase {
		protected VacationDatabase(Session s, org.openntf.domino.Database obj) {
			super(s, obj);
		}
	}

	static class VacationRequest extends org.riverframework.domino.DefaultDocument {

		protected VacationRequest(Database d, org.openntf.domino.Document doc) {
			super(d, doc);
		}

		@Override
		protected VacationRequest afterCreate() {
			setForm("fo_vacation_request");
			return this;
		}
	}

	@Test
	public void testCreateAndGetVacationRequest() {
		assertTrue("The test database could not be opened as a VacationDatabase.", rVacationDatabase.isOpen());

		VacationRequest rDocument = rVacationDatabase.createDocument(VacationRequest.class);

		assertTrue("There is a problem creating a new VacationRequest in the test database.", rDocument.isOpen());

		String universalId = rDocument
				.setField("TEST_FIELD", "YES")
				.setForm("TestForm")
				.save(false)
				.getUniversalId();

		rDocument = null;

		rDocument = rVacationDatabase.getDocument(VacationRequest.class, universalId);

		assertTrue("There is a problem getting a VacationRequest created in the test database.", rDocument.isOpen());
	}

	@Test
	public void testGetMainReplica() {
		fail("Not implemented yet.");
	}

	@Test
	public void testSearch() {
		assertTrue("The test database could not be instantiated.", rDatabase != null);
		assertTrue("The test database could not be opened.", rDatabase.isOpen());

		DocumentCollection col = null;
		col = rDatabase.getAllDocuments().removeAll();

		RandomString rs = new RandomString(10);

		for (int i = 0; i < 10; i++) {
			rDatabase.createDocument(DefaultDocument.class)
					.setForm(TEST_FORM)
					.setField("Value", rs.nextString())
					.save();
		}

		rDatabase.createDocument(DefaultDocument.class)
				.setForm(TEST_FORM)
				.setField("Value", "THIS_IS_THE_DOC")
				.save();

		col = null;
		col = rDatabase.search("THIS IS IMPOSSIBLE TO FIND");
		assertFalse("The search returns values for a query that would returns nothing.", col.hasNext());

		col = null;
		col = rDatabase.search("THIS_IS_THE_DOC");
		assertTrue("The search does not returns values for a query that would returns something.", col.hasNext());
	}
}
