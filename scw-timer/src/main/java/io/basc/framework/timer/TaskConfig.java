package io.basc.framework.timer;

public interface TaskConfig {
	String getTaskId();

	Task getTask();

	TaskListener getTaskListener();
}
