package org.openntf.domino.xots.tasks;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.openntf.domino.xots.tasks.AbstractXotsExecutor.XotsFutureTask;

public interface XotsExecutorService extends ScheduledExecutorService {

	List<XotsFutureTask<?>> getTasks(Comparator<XotsFutureTask<?>> comparator);

	<V> ScheduledFuture<V> schedule(Callable<V> callable, Scheduler scheduler);

	ScheduledFuture<?> schedule(Runnable runnable, Scheduler scheduler);

	ScheduledFuture<?> scheduleTasklet(String moduleName, String className, Scheduler scheduler, Object... ctorArgs);

	ScheduledFuture<?> runTasklet(String moduleName, String className, Object... ctorArgs);

}
