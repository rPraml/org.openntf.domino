package org.openntf.domino.config;

import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;
import javolution.util.FastSet;

import org.openntf.domino.Database;
import org.openntf.domino.Session;
import org.openntf.domino.commons.Hash;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.tasklet.TaskletExecutor;
import org.openntf.tasklet.Tasklet;

/**
 * This is the interface to the ODA-Database
 * 
 * @author Roland Praml, FOCONIS AG
 * @deprecated because it is not production ready!
 */
@Deprecated
public enum Configuration {
	;

	private static TaskletExecutor executor_;
	private static Database odaDb_;
	private static boolean initialized_ = false;
	public static String ODA_NSF = "oda.nsf"; // will be overriden by Notes.ini "ODA_NSF" entry

	protected static Set<ConfigurationObject> dirtyObjects = new FastSet<ConfigurationObject>();
	private static Map<String, String> md5Cache_ = new FastMap<String, String>().atomic();

	/**
	 * The shutdown hook handles proper shutdown.
	 */
	private static Runnable SHUTDOWN_HOOK = new Runnable() {

		@Override
		public void run() {
			executor_ = null;
			// TODO LifeCycleManager.removeCleanupHook(SHUTDOWN_HOOK);
		}
	};

	/**
	 * The object-flusher processes the dirtyObjects-set and saves the configurationObjects from time to time
	 */
	@Tasklet(session = Tasklet.Session.NATIVE)
	private static class ObjectFlusher implements Runnable {

		@Override
		public void run() {
			synchronized (dirtyObjects) {
				for (ConfigurationObject obj : dirtyObjects) {
					obj.syncCache();
				}
				dirtyObjects.clear();
			}

		}
	};

	/**
	 * Adds a configuration Object to the dirty-set so that it can be flushed the next few seconds
	 * 
	 */
	public static void addDirty(final ConfigurationObject configurationObject) {
		synchronized (dirtyObjects) {
			dirtyObjects.add(configurationObject);
		}

	}

	/**
	 * Sets up the executor, if no one exists
	 * 
	 * @return
	 */
	protected static synchronized TaskletExecutor getExecutor() {
		if (executor_ == null) {
			if (Factory.isStarted()) {
				// executor_ = new XotsExecutor(2,4, "Config");
				// executor_.scheduleAtFixedRate(new ObjectFlusher(), 5, 15, TimeUnit.SECONDS);
				// TODO LifeCycleManager.addCleanupHook(SHUTDOWN_HOOK);
			}
		}
		return executor_;
	}

	/**
	 * returns the ODA.NSF Database
	 * 
	 * @return the ODA.NSF Database
	 */
	protected static Database getOdaDb() {
		if (initialized_)
			return odaDb_;
		return initOdaDb();
	}

	/**
	 * Initializes the ODA.NSF. Tries to read "ODA_NSF" value from notes.ini.
	 * 
	 * @return
	 */
	private static Database initOdaDb() {
		if (initialized_)
			return odaDb_;
		synchronized (ODA_NSF) {
			if (initialized_)
				return odaDb_;
			try {
				// ODA-DB is thread safe, so we may cache it :)
				Session sess = Factory.getSession(SessionType.CURRENT);
				String s = sess.getEnvironmentString("ODA_NSF");
				if (!s.isEmpty()) {
					ODA_NSF = s;
					Factory.println("INFO", "Using notes.ini variable ODA_NSF=" + ODA_NSF);
				}
				Database db = sess.getDatabase(ODA_NSF);
				if (db == null) {
					Factory.println("WARNING", "cannot find " + ODA_NSF + " as user " + sess.getEffectiveUserName()
							+ ". - using default values.");
				} else if (!db.isOpen()) {
					Factory.println("ERROR", "cannot open " + ODA_NSF + " as user " + sess.getEffectiveUserName()
							+ ". - using default values.");
				} else {
					odaDb_ = db;
					Factory.println(Configuration.class, "Using " + db + " as configuration database.");
				}
			} catch (Exception e) {
				Factory.println("ERROR", "cannot open " + ODA_NSF + ": " + e.toString() + " - using default values.");
			}
			initialized_ = true;
		}
		return odaDb_;
	}

	public static ServerConfiguration getServerConfiguration(final String serverName) {
		return new ServerConfiguration(serverName);
	}

	public static ServerConfiguration getServerConfiguration() {
		return new ServerConfiguration(Factory.getLocalServerName());
	}

	/**
	 * Utility method, to compute MD5 sum of a string.
	 * 
	 * @param input
	 *            The string to hash
	 * @return The MD5 sum of the string
	 */
	public static String MD5(final String input) {
		// TODO: Move md5 cache to DominoUtils!
		String ret = md5Cache_.get(input);
		if (ret == null) {
			ret = Hash.md5(input);
			md5Cache_.put(input, ret);
		}
		return ret;
	}

	public static String computeUNID(final String input, final Database db) {
		return MD5(input).substring(0, 16).concat(db.getReplicaID());
	}

	/**
	 * Return the XOTS configuration for the given tasklet in the given database.
	 * 
	 * The lookup is done by computed UNID: MD5(taskletName).subString(0,16) + source.ReplicaID) This gives the document a readable creation
	 * time and we use additional 64 bits for tasklet name. As replicaID is (or should!!! be) unique in Domino Installations, it is nearly
	 * impossible to get a collision unless you have thousands of tasklets in your database
	 * 
	 * @param source
	 *            the source DB
	 * @param taskletName
	 *            the taskletName in this DB
	 * @return the configurationProperties
	 */
	public static XotsConfiguration getXotsNSFConfiguration(final String dbPath, final String taskletName) {
		return new XotsConfiguration(dbPath, taskletName, true);
	}

	public static XotsConfiguration getXotsBundleConfiguration(final String bundle, final String taskletName) {
		return new XotsConfiguration(bundle, taskletName, false);
	}

}
