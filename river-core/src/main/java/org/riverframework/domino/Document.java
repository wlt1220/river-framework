package org.riverframework.domino;

import java.util.Map;
import java.util.Vector;

/**
 * Loads an document
 * <p>
 * This is a javadoc test
 * 
 *  @author mario.sotil@gmail.com
 *  @version 0.0.x
 */

public interface Document extends org.riverframework.Document {
	public static final String FIELD_CLASS = Session.FIELD_PREFIX + "class";
	public static final String FIELD_ID = Session.FIELD_PREFIX + "id";
	public static final boolean FORCE_SAVE = true;

	@Override
	public org.riverframework.domino.Database getDatabase();

	public String getForm();

	public String getUniversalId();

	public org.riverframework.domino.Document setForm(String form);

	@Override
	public org.riverframework.domino.Document setField(String field, Object value);

	@Override
	public Vector<Object> getField(String field);

	public Map<String, Vector<Object>> getFields();
	
	@Override
	public org.riverframework.domino.Document setModified(boolean m);

	@Override
	public org.riverframework.domino.Document delete();

	public org.riverframework.domino.Document save(boolean force);

	@Override
	public org.riverframework.domino.Document save();

	@Override
	public org.riverframework.domino.Document recalc();

}
