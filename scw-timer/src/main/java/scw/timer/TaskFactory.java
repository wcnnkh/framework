package scw.timer;

import scw.beans.annotation.AutoImpl;
import scw.core.IteratorCallback;
import scw.timer.support.DefaultTaskFactory;

@AutoImpl({ DefaultTaskFactory.class })
public interface TaskFactory {
	/**
	 * 获取可用的任务
	 * 
	 * @param taskId
	 * @return 为空说明任务不存在或已被取消
	 */
	TaskConfig getTaskConfig(String taskId);

	/**
	 * 当添加任务时会注册
	 * 
	 * @param taskConfig
	 * @return
	 */
	boolean register(TaskConfig taskConfig);

	/**
	 * 当取消任务时会调用
	 * 
	 * @param taskConfig
	 * @return
	 */
	boolean unregister(TaskConfig taskConfig);

	/**
	 * 扫描可用于注册的新任务
	 * 
	 * @param iteratorCallback
	 */
	void iteratorRegisteredTaskConfig(IteratorCallback<TaskConfig> iteratorCallback);
}
