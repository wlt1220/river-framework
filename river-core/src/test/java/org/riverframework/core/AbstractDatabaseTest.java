package org.riverframework.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.riverframework.Context;
import org.riverframework.Database;
import org.riverframework.Document;
import org.riverframework.DocumentIterator;
import org.riverframework.Field;
import org.riverframework.RandomString;
import org.riverframework.Session;
import org.riverframework.Unique;
import org.riverframework.View;

public abstract class AbstractDatabaseTest {
	final String TEST_FORM = "TestForm";
	final String TEST_VIEW = "TestView";
	final String TEST_GRAPH = "TestGraph";

	protected Session session = null;
	protected Database database = null;
	protected Database vacationDatabase = null;
	protected Context context = null;

	@Before
	public void open() {
		// Opening the test context in the current package
		try {
			if (context == null) {
				Class<?> clazz = Class.forName(this.getClass().getPackage().getName() + ".Context");
				if (org.riverframework.Context.class.isAssignableFrom(clazz)) {
					Constructor<?> constructor = clazz.getDeclaredConstructor();
					constructor.setAccessible(true);
					context = (Context) constructor.newInstance();
				}

				session = context.getSession();
				database = session.getDatabase(DefaultDatabase.class, context.getTestDatabaseServer(), context.getTestDatabasePath());
				vacationDatabase = session.getDatabase(VacationDatabase.class, context.getTestDatabaseServer(),
						context.getTestDatabasePath());
				database.getAllDocuments().deleteAll();
				vacationDatabase.getAllDocuments().deleteAll();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@After
	public void close() {
		context.closeSession();
	}

	@Test
	public void testOpenedDatabase() {
		assertTrue("The test database could not be opened.", database.isOpen());
		assertFalse("The file path could not be detected.", database.getFilePath().equals(""));
		assertFalse("The database name could not be detected.", database.getName().equals(""));
	}

	// @Test
	// public void testCreateAndGetGraph() {
	// assertTrue("The test database could not be opened.", rDatabase.isOpen());
	//
	// org.riverframework.development.Relation rRelation =
	// rDatabase.createRelation(TEST_GRAPH);
	//
	// assertTrue("There is a problem creating a new graph in the test database.",
	// rRelation.isOpen());
	//
	// String universalId =
	// rRelation.save(org.riverframework.Document.FORCE_SAVE).getUniversalId();
	// rRelation = null;
	//
	// rRelation = rDatabase.getRelation(universalId);
	//
	// assertTrue("There is a problem getting the graph created in the test database.",
	// rRelation.isOpen());
	// }

	@Test
	public void testCreateAndGetView() {
		assertTrue("The test database could not be instantiated.", database != null);
		assertTrue("The test database could not be opened.", database.isOpen());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String name = "VIEW_" + sdf.format(new Date());
		String form = "FORM_" + sdf.format(new Date());
		View view = database.createView(name, "SELECT Form = \"" + form + "\"");

		assertTrue("There is a problem creating the view in the test database.", view.isOpen());

		view.close();
		view = null;

		int i = 0;
		for (i = 0; i < 10; i++) {
			database.createDocument().setField("Form", form).setField("Value", i).save();
		}

		view = database.getView(name);
		assertTrue("There is a problem opening the last view created in the test database.", view.isOpen());

		DocumentIterator it = view.getAllDocuments();

		i = 0;
		while (it.hasNext()) {
			i++;
			it.next();
		}
		assertTrue("There is a problem with the documents indexed in the last view.", i == 10);

		it = view.getAllDocuments();

		i = 0;
		while (it.hasNext()) {
			i++;
			Document doc = it.next();
			doc.delete();
		}

		view.refresh();
		i = 0;
		it = view.getAllDocuments();
		while (it.hasNext()) {
			i++;
			it.next();
		}

		assertTrue("There is a problem with the last documents created when we try to delete them.", i == 0);

		view.delete();

		assertFalse("There is a problem deleting the last view created.", view.isOpen());
	}

	static class VacationDatabase extends AbstractDatabase {
		protected VacationDatabase(Session s, org.riverframework.wrapper.Database<?> obj) {
			super(s, obj);
		}

		@Override
		public Class<? extends org.riverframework.Document> detectClass(org.riverframework.wrapper.Document<?> _doc) {
			String form = _doc.getFieldAsString("Form").toLowerCase();
			if (form.equals("fo_vacation_request"))
				return VacationRequest.class;

			return null;
		}
	}

	static class VacationRequest extends AbstractDocument<VacationRequest> {
		protected VacationRequest(Database d, org.riverframework.wrapper.Document<?> _d) {
			super(d, _d);
		}

		@Override
		protected VacationRequest afterCreate() {
			setField("Form", "fo_vacation_request");
			return this;
		}

		@Override
		protected VacationRequest getThis() {
			return this;
		}
	}

	@Test
	public void testCreateAndGetVacationRequest() {
		assertTrue("The test database could not be opened as a VacationDatabase.", vacationDatabase.isOpen());

		VacationRequest doc = vacationDatabase.createDocument(VacationRequest.class);

		assertTrue("There is a problem creating a new VacationRequest in the test database.", doc.isOpen());

		String universalId = doc.setField("TEST_FIELD", "YES").setField("Form", "TestForm").save().getObjectId();

		doc = null;
		doc = vacationDatabase.getDocument(VacationRequest.class, universalId);

		assertTrue("There is a problem getting a VacationRequest created in the test database.", doc.isOpen());
	}

	@Test
	public void testSearch() {
		assertTrue("The test database could not be instantiated.", database != null);
		assertTrue("The test database could not be opened.", database.isOpen());

		DocumentIterator col = database.getAllDocuments().deleteAll();

		RandomString rs = new RandomString(10);

		for (int i = 0; i < 10; i++) {
			database.createDocument(DefaultDocument.class).setField("Form", TEST_FORM).setField("Value", rs.nextString()).save();
		}

		database.createDocument(DefaultDocument.class).setField("Form", TEST_FORM).setField("Value", "THIS_IS_THE_DOC").save();

		database.refreshSearchIndex();

		col = null;
		col = database.search("THIS IS IMPOSSIBLE TO FIND");
		assertTrue("The search returns values for a query that would returns nothing.", !col.hasNext());

		col = null;
		col = database.search("THIS_IS_THE_DOC");
		assertTrue("The search does not returns values for a query that would returns something.", col.hasNext());
	}

	@Test
	public void testGetDocumentCollection() {
		assertTrue("The vacation database could not be instantiated.", vacationDatabase != null);
		assertTrue("The vacation database could not be opened.", vacationDatabase.isOpen());

		vacationDatabase.getAllDocuments().deleteAll();

		vacationDatabase.createDocument(VacationRequest.class).setField("Requester", "John").setField("Time", 30).save();

		vacationDatabase.createDocument(VacationRequest.class).setField("Requester", "Kathy").setField("Time", 25).save();

		vacationDatabase.createDocument(VacationRequest.class).setField("Requester", "Michael").setField("Time", 27).save();

		DocumentIterator col = vacationDatabase.getAllDocuments();

		for (Document doc : col) {
			assertTrue("It could not possible load the vacation request object from the DocumentList.", doc.isOpen());
			assertTrue("The vacation request object from the DocumentList is an instance from " + doc.getClass().getName()
					+ ", and not from VacationRequest.", doc.getClass().getSimpleName().contains("VacationRequest"));
		}
	}

	static class Person extends AbstractDocument<Person> implements Document, Unique {
		protected Person(Database d, org.riverframework.wrapper.Document<?> _d) {
			super(d, _d);
		}

		@Override
		public String getIndexName() {
			return "vi_ap_people_index";
		}

		@Override
		public String getId() {
			return getFieldAsString("ca_pe_name");
		}

		@Override
		public org.riverframework.Document generateId() {
			// Do nothing
			return this;
		}

		@Override
		public org.riverframework.Document setId(String arg0) {
			setField("ca_pe_name", arg0);
			return this;
		}

		@Override
		protected Person getThis() {
			return this;
		}

	}

	@Test
	public void testGetDocument() {
		assertTrue("The test database could not be instantiated.", database != null);
		assertTrue("The test database could not be opened.", database.isOpen());

		DocumentIterator iterator = database.getAllDocuments().deleteAll();
		assertFalse("The database still has documents.", iterator.hasNext());

		database.createDocument(Person.class).setId("John").setField("Form", "fo_ap_people").setField("Age", 30).save();

		database.createDocument(Person.class).setId("Kathy").setField("Form", "fo_ap_people").setField("Age", 25).save();

		database.createDocument(Person.class).setId("Jake").setField("Form", "fo_ap_people").setField("Age", 27).save();

		Document p = database.getDocument(Person.class, "Jake");
		assertTrue("It could not possible load the person object for Jake.", p.isOpen());
		assertTrue("It could not possible get the Jake's age.", p.getFieldAsInteger("Age") == 27);

		p = database.getDocument(Person.class, "John");
		assertTrue("It could not possible load the person object for John.", p.isOpen());
		assertTrue("It could not possible get the John's age.", p.getFieldAsInteger("Age") == 30);

		p = database.getDocument(Person.class, "Kathy");
		assertTrue("It could not possible load the person object for Kathy.", p.isOpen());
		assertTrue("It could not possible get the Kathy's age.", p.getFieldAsInteger("Age") == 25);

		String unid = p.getObjectId();
		p = null;
		p = database.getDocument(unid);
		assertTrue("It should be possible to load a person object for Kathy with its Universal Id.", p.isOpen());

		p = null;
		p = database.getDocument("Kathy");
		assertFalse("It should not be possible to load a person object for Kathy without indicate its class.", p.isOpen());
	}

	@Test
	public void testGetFields() {
		assertTrue("The test database could not be instantiated.", database != null);
		assertTrue("The test database could not be opened.", database.isOpen());

		database.getAllDocuments().deleteAll();

		database.createDocument(Person.class).setId("Kathy").setField("Form", "fo_ap_people").setField("Age", 25).save().close();

		Document p = database.getDocument(Person.class, "Kathy");
		assertTrue("It could not possible load the person object for Kathy.", p.isOpen());

		Map<String, Field> fields = p.getFields();

		assertTrue("It could not possible get the fields from the Kathy's document.", fields.size() > 0);

		Field field = fields.get("ca_pe_name");
		String value = field.get(0).toString();

		assertTrue("It could not possible get the field Name from the Kathy's document.", value.equals("Kathy"));

		field = fields.get("Age");
		int age = ((Double) field.get(0)).intValue();

		assertTrue("It could not possible get the field Age from the Kathy's document.", age == 25);

	}

}
