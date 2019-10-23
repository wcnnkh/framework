package scw.timer.support;

import scw.timer.Task;
import scw.timer.TaskConfig;
import scw.timer.TaskListener;

public class SimpleTaskConfig implements TaskConfig {
	private final String taskId;
	private final Task task;
	private final TaskListener taskListener;
	private final boolean distributed;

	public SimpleTaskConfig(String taskId, Task task, TaskListener taskListener, boolean distributed) {
		this.taskId = taskId;
		this.task = task;
		this.taskListener = taskListener;
		this.distributed = distributed;
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

	public boolean isDistributed() {
		return distributed;
	}

}
