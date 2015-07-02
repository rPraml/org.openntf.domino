package org.openntf.domino.impl;

import org.openntf.domino.View;
import org.openntf.domino.commons.IO;
import org.openntf.domino.commons.IRequest;
import org.openntf.domino.commons.IRequestLifeCycle;
import org.openntf.domino.commons.StandardLifeCycle;
import org.openntf.domino.exceptions.BackendBridgeSanityCheckException;

import com.ibm.domino.napi.c.BackendBridge;

public class DominoLifeCycle extends StandardLifeCycle implements IRequestLifeCycle {

	@Override
	public void beforeRequest(final IRequest request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterRequest() {
		BaseNonThreadSafe.setAllowAccessAcrossThreads(false);
		DateTime.cleanupThread();
	}

	@Override
	public void startup() {
		// TODO Auto-generated method stub
		super.startup();
		verifyIGetEntryByKey();
	}

	/**
	 * there is one weird thing in getViewEntryByKeyWithOptions. IBM messed up something in the JNI calls.
	 * 
	 * a correct call would look like this:
	 * 
	 * <pre>
	 * jclass activityClass = env -&gt; GetObjectClass(dummyView);
	 * jmethodID mID = env -&gt; GetMethodID(activityClass, &quot;iGetEntryByKey&quot;, &quot;...&quot;);
	 * entry = env -&gt; CallIntMethod(obj, mID);
	 * </pre>
	 * 
	 * IBM's code probably looks like this:
	 * 
	 * <pre>
	 * jclass activityClass = env->GetObjectClass(lotus.domino.local.View); <font color=red>&lt;--- This is wrong!</font>
	 * jmethodID mID = env->GetMethodID(activityClass, "iGetEntryByKey", "..."); 
	 * entry = env->CallIntMethod(obj, mID);
	 * </pre>
	 * 
	 * so we get the method-pointer mID for the "lotus.domino.local.View" and we call this method on an "org.openntf.domino.impl.View".
	 * 
	 * This is something that normally wouldn't work. But C/C++ does no sanity checks if it operates on the correct class and will call a
	 * (more or less) random method that is on position "mID". (compare to a 'goto 666')
	 * 
	 * To get that working, we must reorder the methods in the View class, so that "iGetEntryByKey" is on the correct place. Every time you
	 * add or remove methods to the View class (and maybe also to the Base class) the position must be checked again. This is done in the
	 * this method:
	 * <ol>
	 * <li>We call getViewEntryByKeyWithOptions with the "key parameters" dummyView, null, 42.</li>
	 * <li>This will result in a call to dummyView.iGetEntryByKey(null, false, 42);</li>
	 * <li>If iGetEntryByKey is called with a "null" vector and 42 as int, it will throw a "BackendBridgeSanityCheckException" (which we
	 * expect)</li>
	 * <li>If any other mehtod is called it will throw a different exception. (Most likely a NPE, because our view has no delegate)</li>
	 * </ol>
	 * I hope the server would not crash then. I assume this because:
	 * <ul>
	 * <li>null as parameter is less problematic than a Vector that was forced in a String variable</li>
	 * <li>Throwing an exception does not generate a return value that will be forced in a ViewEntry</li>
	 * </ul>
	 */
	public boolean verifyIGetEntryByKey() {
		@SuppressWarnings("deprecation")
		View dummyView = new org.openntf.domino.impl.View();
		try {
			BackendBridge.getViewEntryByKeyWithOptions(dummyView, null, 42);
		} catch (BackendBridgeSanityCheckException allGood) {
			IO.println("Operation of BackendBridge.getViewEntryByKeyWithOptions verified");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			// if you get here, analyze the stack trace and rearrange the "iGetEntryByKey" method in
			// the view to the position that is listed in the stack trace above "getViewEntryByKeyWithOptions"
		}
		// if you do not get an exception, you will have to debug it with "step into"
		IO.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		IO.println("Operation of BackendBridge.getViewEntryByKeyWithOptions FAILED");
		IO.println("Please read the comments in " + getClass().getName());
		IO.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		return false;
	}
}
