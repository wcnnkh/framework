package io.basc.framework.timer.support;

import io.basc.framework.timer.Task;
import io.basc.framework.timer.TaskConfig;
import io.basc.framework.timer.TaskListener;

public class SimpleTaskConfig implements TaskConfig {
	private final String taskId;
	private final Task task;
	private final TaskListener taskListener;

	public SimpleTaskConfig(String taskId, Task task, TaskListener taskListener) {
		this.taskId = taskId;
		this.task = task;
		this.taskListener = taskListener;
	}

	public String getTaskId() {
		return taskId;
	}

	public Task getTask() {
		return task;
	}

	public TaskListener getTaskListener() {
		return taskListener;
	}
}
