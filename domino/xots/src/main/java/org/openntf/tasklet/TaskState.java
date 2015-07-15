package org.openntf.tasklet;

public enum TaskState {
	/** The Task is Queued and will be executed next */

	QUEUED,

	/** The Task is sleeping and will be executed further */
	SLEEPING,

	/** The Task is currently running */
	RUNNING,

	/** The Task is finished */
	DONE,

	ERROR
}