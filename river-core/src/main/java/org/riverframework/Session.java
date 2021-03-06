package org.riverframework;

public interface Session extends Base {
	// TODO: evaluate if this const is necessary or can be removed
	/**
	 * The ELEMENT_PREFIX is used to define the of elements as Views, to indicate that elements are for exclusive use of
	 * the framework's core.
	 */
	public static final String ELEMENT_PREFIX = "RIVER_";

	// TODO: evaluate if this const is necessary or can be removed
	/**
	 * The FIELD_PREFIX is used to define field's names, to indicate that fields are for exclusive use of the
	 * framework's core.
	 */
	public static final String FIELD_PREFIX = "RIVER_";

	/**
	 * Returns the object that wraps the native object. For example, if the wrapper loaded is
	 * River.LOTUS_DOMINO, and the object is an instance of org.riverframework.core.DefaultDocument,
	 * getNativeObject() will return an object that implements the org.riverframework.wrapper.Document interface.
	 * 
	 * @return the object used to wrap the native object
	 */
	@Override
	public org.riverframework.wrapper.Session<?> getWrapperObject();
	
	/**
	 * Creates a new database.  
	 * 
	 * @param parameters Depends on what wrapper is being used.
	 * @return a DefaultDatabase object.
	 */
	public <U extends Database> U createDatabase(String... location);
	
	/**
	 * Creates a new database.
	 * 
	 * @param type The class that implements org.riverframework.Database
	 * @param parameters Depends on what wrapper is being used.
	 * @return an object from the class selected in the parameter 'type'
	 */
	public <U extends Database> U createDatabase(Class<U> type, String... location);
	
	/**
	 * Returns a core Database object after open a wrapper Database, using the parameters indicated.
	 * 
	 * @param parameters
	 *            the parameters needed to open an existent wrapper Database. How this parameters must to be set will
	 *            depend on how the wrapper loaded is implemented.
	 * @return a core Database object
	 */
	public <U extends Database> U getDatabase(String... location);

	/**
	 * Returns a core Database object after open a wrapper Database, using the parameters indicated.
	 * 
	 * @param clazz
	 *            a class that inherits from DefaultDatabase and implements the core Database interface.
	 * @param parameters
	 *            the parameters needed to open an existent wrapper Database. How this parameters must to be set will
	 *            depend on how the wrapper loaded is implemented.
	 * @return a core Database object
	 */
	public <U extends Database> U getDatabase(Class<U> type, String... location);

	/**
	 * Returns true if the wrapper was loaded and the session opened.
	 * 
	 * @return true if it's opened
	 */
	public boolean isOpen();

	/**
	 * Returns the current user name logged with this session.
	 * 
	 * @return the current user name
	 */
	public String getUserName();

	/**
	 * Close the session and frees its resources, handles, etc.
	 */
	@Override
	public void close();
}
