package org.openntf.domino.xots;

import java.util.Arrays;

import org.openntf.domino.Database;
import org.openntf.domino.logging.BaseOpenLogItem;
import org.openntf.domino.xots.Tasklet.Interface;

public enum XotsUtil {
	;
	/**
	 * Returns the schedule settings of the given class
	 * 
	 */
	public static ScheduleData getSchedule(final String replicaId, final Class<?> clazz) throws IllegalAccessException,
	InstantiationException {
		Tasklet annot = clazz.getAnnotation(Tasklet.class);
		String[] effectiveSchedDefs = null;

		if (annot == null)
			return null;
		String[] schedDefs = annot.schedule();
		System.out.println("### Tasklet: " + clazz.getName() + " " + replicaId + " Sched: " + Arrays.toString(schedDefs));
		for (String schedDef : schedDefs) {
			if (!schedDef.equals("")) {
				effectiveSchedDefs = schedDefs;
				if (schedDef.equals("dynamic")) {
					Tasklet.Interface ti = (Interface) clazz.newInstance();
					effectiveSchedDefs = ti.getDynamicSchedule();
					break;
				}
			}
		}

		if (effectiveSchedDefs == null)
			return null;
		return new ScheduleDataNSF(replicaId, clazz.getName(), effectiveSchedDefs, annot.onAllServers());
	}

	/**
	 * Returns the schedule settings of the given object
	 */
	public static String[] getSchedule(final Object obj) {
		Tasklet annot = obj.getClass().getAnnotation(Tasklet.class);
		String[] effectiveSchedDefs = null;

		if (annot != null) {
			String[] schedDefs = annot.schedule();

			for (String schedDef : schedDefs) {
				if (!schedDef.equals("")) {
					effectiveSchedDefs = schedDefs;
					if (schedDef.equals("dynamic")) {
						Tasklet.Interface ti = (Interface) obj;
						effectiveSchedDefs = ti.getDynamicSchedule();
						break;
					}
				}
			}

		}
		return effectiveSchedDefs;
	}

	public static void handleException(final Throwable t, final Database currDb) {
		BaseOpenLogItem ol = new BaseOpenLogItem();
		ol.setCurrentDatabase(currDb);
		ol.logError(t);
	}
}
