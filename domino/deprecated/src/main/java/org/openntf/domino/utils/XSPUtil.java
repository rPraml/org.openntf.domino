package org.openntf.domino.utils;

import java.lang.reflect.Method;

import org.openntf.domino.Database;
import org.openntf.domino.Document;
import org.openntf.domino.Session;
import org.openntf.domino.View;
import org.openntf.domino.ViewEntry;
import org.openntf.domino.WrapperFactory;
import org.openntf.domino.utils.Factory.SessionType;

// TODO: Auto-generated Javadoc
/**
 * The Enum XSPUtil.
 */
@Deprecated
public enum XSPUtil {

	/** The instance. */
	INSTANCE;

	/**
	 * Wrap.
	 * 
	 * @param doc
	 *            the doc
	 * @return the document
	 */
	public static Document wrap(final lotus.domino.Document doc) {
		try {
			lotus.domino.Database db = doc.getParentDatabase();
			WrapperFactory wf = Factory.getWrapperFactory();
			Session session = wf.fromLotus(db.getParent(), Session.SCHEMA, wf);
			Database wrappedDB = wf.fromLotus(db, Database.SCHEMA, session);
			return wf.fromLotus(doc, Document.SCHEMA, wrappedDB);
		} catch (lotus.domino.NotesException ne) {
			return null;
		}
	}

	/**
	 * Wrap.
	 * 
	 * @param entry
	 *            the entry
	 * @return the view entry
	 */
	public static ViewEntry wrap(final lotus.domino.ViewEntry entry) {
		try {
			Object parent = entry.getParent();
			lotus.domino.View view;
			if (parent instanceof lotus.domino.ViewEntryCollection) {
				view = ((lotus.domino.ViewEntryCollection) parent).getParent();
			} else if (parent instanceof lotus.domino.ViewNavigator) {
				view = ((lotus.domino.ViewNavigator) parent).getParentView();
			} else {
				view = (lotus.domino.View) parent;
			}
			lotus.domino.Database db = view.getParent();
			WrapperFactory wf = Factory.getWrapperFactory();
			Session session = wf.fromLotus(db.getParent(), Session.SCHEMA, wf);
			Database wrappedDB = wf.fromLotus(db, Database.SCHEMA, session);
			View wrappedView = wf.fromLotus(view, View.SCHEMA, wrappedDB);
			//			if (parent instanceof lotus.domino.ViewEntryCollection) {
			//				ViewEntryCollection vec = Factory.fromLotus((lotus.domino.ViewEntryCollection) parent, ViewEntryCollection.SCHEMA,
			//						wrappedView);
			//				return Factory.fromLotus(entry, ViewEntry.SCHEMA, vec);
			//			} else if (parent instanceof lotus.domino.ViewNavigator) {
			//				ViewNavigator vnav = Factory.fromLotus((lotus.domino.ViewNavigator) parent, ViewNavigator.SCHEMA, wrappedView);
			//				return Factory.fromLotus(entry, ViewEntry.SCHEMA, vnav);
			//			} else {
			return wf.fromLotus(entry, ViewEntry.SCHEMA, wrappedView);
			//			}
		} catch (lotus.domino.NotesException ne) {
			return null;
		}
	}

	/**
	 * Gets the current database.
	 * 
	 * @return the current database
	 */
	public static Database getCurrentDatabase() {
		try {
			lotus.domino.Database db = (lotus.domino.Database) resolveVariable("database");
			if (db instanceof org.openntf.domino.Database) {
				return (org.openntf.domino.Database) db;
			} else {
				WrapperFactory wf = Factory.getWrapperFactory();
				Session session = wf.fromLotus(db.getParent(), Session.SCHEMA, wf);
				return wf.fromLotus(db, Database.SCHEMA, session);
			}
		} catch (Exception ne) {
			DominoUtils.handleException(ne);
			return null;
		}
	}

	/**
	 * Gets the current session.
	 * 
	 * @return the current session
	 * 
	 */
	public static Session getCurrentSession() {
		return Factory.getSession(SessionType.CURRENT);
	}

	/**
	 * Gets the current session as signer.
	 * 
	 * @return the current session as signer
	 * @deprecated use {@link Factory#getSession(SessionType)} instead
	 */
	@Deprecated
	public static Session getCurrentSessionAsSigner() {
		return Factory.getSession(SessionType.SIGNER);
	}

	/**
	 * Gets the current session as signer with full access.
	 * 
	 * @return the current session as signer with full access
	 * @deprecated use {@link Factory#getSession(SessionType)} instead
	 */

	@Deprecated
	public static Session getCurrentSessionAsSignerWithFullAccess() {
		return Factory.getSession(SessionType.SIGNER_FULL_ACCESS);
	}

	/**
	 * Resolve variable.
	 * 
	 * @param varName
	 *            the var name
	 * @return the object
	 * @throws Exception
	 *             the exception
	 */
	@Deprecated
	public static Object resolveVariable(final String varName) throws Exception {
		// TODO RPr move to Xpage-Plugin
		Class<?> facesContextClass = Class.forName("javax.faces.context.FacesContext", true, Factory.getClassLoader());
		Method getCurrentInstance = facesContextClass.getMethod("getCurrentInstance");
		Method getApplication = facesContextClass.getMethod("getApplication");
		Class<?> applicationClass = Class.forName("javax.faces.application.Application", true, Factory.getClassLoader());
		Method getVariableResolver = applicationClass.getMethod("getVariableResolver");
		Class<?> variableResolverClass = Class.forName("javax.faces.el.VariableResolver", true, Factory.getClassLoader());
		Method resolveVariable = variableResolverClass.getMethod("resolveVariable", facesContextClass, String.class);

		Object facesContext = getCurrentInstance.invoke(null);
		Object application = getApplication.invoke(facesContext);
		Object variableResolver = getVariableResolver.invoke(application);
		return resolveVariable.invoke(variableResolver, facesContext, varName);
	}
}
