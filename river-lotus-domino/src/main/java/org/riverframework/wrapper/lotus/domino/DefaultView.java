package org.riverframework.wrapper.lotus.domino;

import java.util.logging.Logger;

import lotus.domino.NotesException;

import org.riverframework.River;
import org.riverframework.RiverException;
import org.riverframework.wrapper.Document;
import org.riverframework.wrapper.DocumentIterator;
import org.riverframework.wrapper.View;

class DefaultView implements org.riverframework.wrapper.View {
	private static final Logger log = River.LOG_WRAPPER_LOTUS_DOMINO;
	protected org.riverframework.wrapper.Session _session = null;
	protected lotus.domino.View __view = null;
	private String objectId = null;

	protected DefaultView(org.riverframework.wrapper.Session s, lotus.domino.View v) {
		__view = v;
		_session = s;
		objectId = calcObjectId(__view);
	}

	@Override
	public String getObjectId() {
		return objectId;
	}

	public static String calcObjectId(lotus.domino.View __view) {
		String objectId = "";

		if (__view != null) {
			try {
				lotus.domino.Database __database = __view.getParent();

				StringBuilder sb = new StringBuilder();
				sb.append(__database.getServer());
				sb.append(River.ID_SEPARATOR);
				sb.append(__database.getFilePath());
				sb.append(River.ID_SEPARATOR);
				sb.append(__view.getName());
//				sb.append(River.ID_SEPARATOR);
//				sb.append(__view.hashCode());

				objectId = sb.toString();
			} catch (NotesException e) {
				throw new RiverException(e);
			}	
		}

		return objectId;
	}

	@Override
	public lotus.domino.View getNativeObject() {
		return __view;
	}

	@SuppressWarnings("unused")
	@Override
	public Document getDocumentByKey(String key) {
		lotus.domino.Document __doc = null;

		try {
			__doc = __view.getDocumentByKey(key, true);
		} catch (NotesException e) {
			try {
				if (__doc != null) 
					__doc.recycle();				
			} catch (NotesException e1) {
				// Do nothing
			} finally {
				__doc = null;
			}

			throw new RiverException(e);
		}

		Document doc = _session.getFactory().getDocument(__doc);

		return doc;
	}

	@Override
	public boolean isOpen() {
		return __view != null;
	}

	@Override
	public DocumentIterator getAllDocuments() {
		lotus.domino.ViewEntryCollection _col;
		try {
			_col = __view.getAllEntries();
		} catch (NotesException e) {
			throw new RiverException(e);
		}

		DocumentIterator result = _session.getFactory().getDocumentIterator(_col);
		return result;
	}

	@Override
	public DocumentIterator getAllDocumentsByKey(Object key) {
		lotus.domino.DocumentCollection _col;
		try {
			_col = __view.getAllDocumentsByKey(key, true);
		} catch (NotesException e) {
			throw new RiverException(e);
		}

		DocumentIterator result = _session.getFactory().getDocumentIterator(_col);
		return result;
	}

	@Override
	public View refresh() {
		try {
			__view.refresh();
		} catch (NotesException e) {
			throw new RiverException(e);
		}
		return this;
	}

	@Override
	public void delete() {
		if (__view != null) {
			try {
				__view.remove();
				__view.recycle();
			} catch (NotesException e) {
				throw new RiverException(e);
			} finally {
				__view = null;
			}
		}
	}

	@Override
	public DocumentIterator search(String query) {
		lotus.domino.View __temp = null;
		
		try {
			__temp = __view.getParent().getView(__view.getName());			
			__temp.FTSearch(query);
		} catch (NotesException e) {
			throw new RiverException(e);
		}
		
		DocumentIterator _iterator = _session.getFactory().getDocumentIterator(__temp);
		
		return _iterator;
	}

	@Override
	public void close() {
		try {
			if (__view != null) 
				__view.recycle();			
		} catch (NotesException e) {
			throw new RiverException(e);
		} finally {
			__view = null;
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + "(" + objectId + ")";
	}

	@Override
	public void finalize() {
		log.finest("Finalized: id=" + objectId + " (" + this.hashCode() + ")");
	}
}
