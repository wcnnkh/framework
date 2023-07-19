package io.basc.framework.timer.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.core.Ordered;
import io.basc.framework.timer.TaskConfig;
import io.basc.framework.timer.TaskFactory;

@ConditionalOnParameters(order = Ordered.LOWEST_PRECEDENCE)
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

	public void iteratorRegisteredTaskConfig(Predicate<TaskConfig> action) {
		return;
	}

}
