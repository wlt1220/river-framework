package org.riverframework.core;

import java.lang.reflect.Constructor;

import org.riverframework.ClosedObjectException;
import org.riverframework.River;
import org.riverframework.RiverException;
import org.riverframework.Session;

/**
 * Implements the Session interface. Manages Databases.
 * 
 * @author mario.sotil@gmail.com
 *
 */
public abstract class AbstractSession implements org.riverframework.Session {
	public static final String PREFIX = "RIVER_";

	private org.riverframework.wrapper.Session<?> _session = null;

	protected AbstractSession(org.riverframework.wrapper.Session<?> _s) {
		// Exists only to defeat instantiation.
		_session = _s;
	}

	@Override
	public String getObjectId() {
		if (!isOpen())
			throw new ClosedObjectException("The Session object is closed.");

		return _session.getObjectId();
	}

	@Override
	public org.riverframework.wrapper.Session<?> getWrapperObject() {
		return _session;
	}

	@Override
	public boolean isOpen() {
		return (_session != null && _session.isOpen());
	}

	@Override
	public <U extends org.riverframework.Database> U createDatabase(String... location) {
		return createDatabase(null, location);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends org.riverframework.Database> U createDatabase(Class<U> clazz, String... location) {
		if (!isOpen())
			throw new ClosedObjectException("The Session object is closed.");

		org.riverframework.wrapper.Database<?> _database = _session.createDatabase(location);
		U database = null;
		Class<U> c = clazz;

		if (c == null)
			c = (Class<U>) DefaultDatabase.class;

		if (!AbstractDatabase.class.isAssignableFrom(c))
			throw new RiverException("The clazz parameter must inherit from DefaultDatabase.");

		try {
			Constructor<?> constructor = c.getDeclaredConstructor(Session.class, org.riverframework.wrapper.Database.class);
			constructor.setAccessible(true);
			database = c.cast(constructor.newInstance(this, _database));
		} catch (Exception e) {
			throw new RiverException(e);
		}

		return database;
	}

	@Override
	public <U extends org.riverframework.Database> U getDatabase(String... location) {
		return getDatabase(null, location);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends org.riverframework.Database> U getDatabase(Class<U> clazz, String... location) {
		if (!isOpen())
			throw new ClosedObjectException("The Session object is closed.");

		U database = null;
		Class<U> c = clazz;
		org.riverframework.wrapper.Database<?> _database = null;

		if (c == null)
			c = (Class<U>) DefaultDatabase.class;

		if (!AbstractDatabase.class.isAssignableFrom(c))
			throw new RiverException("The clazz parameter must inherit from DefaultDatabase.");

		if (_database == null || !_database.isOpen()) {
			_database = _session.getDatabase(location);
		}

		if (_database != null && !_database.isOpen()) {
			_database = null;
		}

		try {
			Constructor<?> constructor = c.getDeclaredConstructor(Session.class, org.riverframework.wrapper.Database.class);
			constructor.setAccessible(true);
			database = c.cast(constructor.newInstance(this, _database));
		} catch (Exception e) {
			throw new RiverException(e);
		}

		return database;
	}

	@Override
	public String getUserName() {
		if (!isOpen())
			throw new ClosedObjectException("The Session object is closed.");

		return _session.getUserName();
	}

	/**
	 * It's the really responsible to close the session. It's called by the
	 * close() method. It's hide from the real world as a protected method,
	 * because it never has to be called alone. Only the River factory class can
	 * call it.
	 */
	protected void protectedClose() {
		_session.close();
	}

	@Override
	public void close() {
		// This is for prevent that the session be closed but not purged from
		// the River factory class.
		String wrapper = _session.getClass().getPackage().getName();
		River.closeSession(wrapper);
	}

	@Override
	public String toString() {
		return getClass().getName() + "(" + getWrapperObject().toString() + ")";
	}
}
