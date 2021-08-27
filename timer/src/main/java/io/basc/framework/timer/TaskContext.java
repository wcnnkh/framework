package io.basc.framework.timer;

public interface TaskContext {
	/**
	 * 取消任务
	 * 
	 * @return 是否取消成功
	 */
	boolean cancel();

	TaskConfig getTaskConfig();
}
