package scw.timer.support;

import java.util.concurrent.ConcurrentHashMap;

import scw.core.utils.IteratorCallback;
import scw.timer.TaskConfig;
import scw.timer.TaskFactory;

public final class DefaultTaskFactory implements TaskFactory {
	private final ConcurrentHashMap<String, TaskConfig> taskMap = new ConcurrentHashMap<String, TaskConfig>();

	public TaskConfig getTaskConfig(String taskId) {
		return taskMap.get(taskId);
	}

	public boolean register(TaskConfig taskConfig) {
		return taskMap.putIfAbsent(taskConfig.getTaskId(), taskConfig) == null;
	}

	public boolean unregister(TaskConfig taskConfig) {
		return taskMap.remove(taskConfig.getTaskId()) != null;
	}

	public void iteratorRegisteredTaskConfig(IteratorCallback<TaskConfig> iteratorCallback) {
		return;
	}

}
