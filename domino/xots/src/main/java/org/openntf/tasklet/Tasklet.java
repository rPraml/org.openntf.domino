package org.openntf.tasklet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
public @interface Tasklet {
	public interface Interface {

		/**
		 * Returns the {@link Context} where the Tasklet should run. If <code>null</code> is returned, the {@link Tasklet#scope()}
		 * annotation counts.
		 * 
		 * @return a scope or null.
		 */
		public Scope getScope();

		/**
		 * Notifies the tasklet that it should stop if possible.
		 */
		public void stop();

		/**
		 * Returns a dynamic schedule plan. This method is only called if you specify {@literal @}Tasklet(schedule="dynamic"). Class needs a
		 * default constructor.
		 * 
		 * @return Array of schedule strings
		 */
		public String[] getDynamicSchedule();

		/**
		 * Returns a description of this tasklet
		 * 
		 * @return
		 */
		public String getDescription();

	}

	/**
	 * The Scope determines the concurrency
	 */
	public enum Scope {
		/**
		 * One instance per server is allowed
		 */
		SERVER,

		/**
		 * One instance per template
		 */
		TEMPLATE,

		/**
		 * One instance per application
		 */
		APPLICATION,

		/**
		 * One instance per server and user is allowed
		 */
		SERVER_USER,
		/**
		 * One instance per template and user is allowed
		 */
		TEMPLATE_USER,
		/**
		 * One instance per application and user is allowed
		 */
		APPLICATION_USER,

		/**
		 * No scope, you can run as many instances as you have resource
		 */
		NONE
	}

	/**
	 * The session type for XOTS Tasklets: Note that CLONE, CLONE_FULL_ACCESS, SIGNER, SIGNER_FULL_ACCESS are XPage-Sessions and may be
	 * restricted by maximumInternetAccess
	 * 
	 * @author Roland Praml, FOCONIS AG
	 * 
	 */
	public enum Session {

		/**
		 * Clones the {@link SessionType#CURRENT CURRENT} Session from the current context. This means if you are running in a XPage, you
		 * have the same user session in your XOTS Runnable. If you are running a scheduled servlet, you have a
		 * {@link Factory.SessionType#SESSION_AS_SIGNER SESSION_AS_SIGNER}.
		 * 
		 * This Session may be created with createXPageSession and may be restricted to maximum internet access!
		 */
		CLONE,

		/**
		 * @See {@link #CLONE} but with full access
		 */
		CLONE_FULL_ACCESS,

		/**
		 * Run as signer of that Runnable.class. If the Runnable is inside an NSF. It will run with a named session of the Signer. If the
		 * Runnable is a Plugin-Java object, it will run with the Server-ID
		 * 
		 * This Session may be created with createXPageSession and may be restricted to maximum internet access!
		 */
		SIGNER,

		/**
		 * @See {@link #SIGNER} but with full access
		 */
		SIGNER_FULL_ACCESS,

		/**
		 * Use the native session. Session is created with NotesFactory.createSession()
		 * 
		 * User and Effective user is the current server (or on client the current user) This Session should not be restricted by maximum
		 * internet access!
		 */
		NATIVE,

		/**
		 * Use a full access session. Session is created with NotesFactory.createSessionWithFullAccess()
		 * 
		 * Access is not restricted to readers/authors fields
		 */
		FULL_ACCESS,

		/**
		 * Use a trusted session. Session is created with NotesFactory.createTrustedSession()<br>
		 * 
		 * Applications running on a server installation that need to access databases on a remote server must have either a Trusted Server
		 * relationship, or a Trusted Session. The userID authority that the application is running under must be accounted for in the ACL
		 * of the remote database. That userID is often the serverID.
		 * 
		 * <font color=red>This does NOT yet work</font>
		 */
		TRUSTED,

		/**
		 * Do not create a session
		 */
		NONE
	}

	Tasklet.Session session() default Tasklet.Session.CLONE;

	Tasklet.Scope scope() default Tasklet.Scope.APPLICATION;

	/**
	 * specifies the schedule.
	 * 
	 * Examples;
	 * <ul>
	 * <li><code>cron:0 *&#x2F;15 02-23 * * *</code> to run the tasklet every 15 minutes between 02 and 23 o'clock. See
	 * {@link CronExpression}</li>
	 * <li><code>delay:45m 08:30-22:30 MTWRFSU</code> to run a periodic task with a delay of 45 minutes (45 minutes between runs) between
	 * 08:30 and 22:30. This should be prefered to cron, because cron will start all periodic tasks in the same minute.</li>
	 * <li><code>period:45m 08:30-22:30 MTWRF</code> to run a periodic task every 45 minutes between 08:30 and 22:30. This should be
	 * prefered to cron, because cron will start all periodic tasks in the same minute.</li>
	 * <li><code>manual</code> if you want to execute the schedule manually. (tell http osgi xots run &lt;module&gt; &lt;taskletClass&gt;)</li>
	 * <li><code>dynamic</code> get the dynamic schedule by invoking {@link Tasklet.Interface#getDynamicSchedule()} (this must be the first
	 * and only annotation. Class must have a default constructor.)</li>
	 * 
	 * @return the schedules
	 */
	String[] schedule() default "";

	/**
	 * Should the scheduled tasklet run on all servers?
	 */
	boolean onAllServers() default false;

}
