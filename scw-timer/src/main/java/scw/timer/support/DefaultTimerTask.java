package scw.timer.support;

import scw.locks.Lock;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.timer.TaskConfig;
import scw.timer.TaskListener;
import scw.timer.TaskLockFactory;

public class DefaultTimerTask implements scw.timer.Task {
	private static Logger logger = LoggerUtils.getLogger(DefaultTimerTask.class);
	private final TaskLockFactory taskLockFactory;
	private final TaskConfig config;

	public DefaultTimerTask(TaskLockFactory taskLockFactory, TaskConfig config) {
		this.taskLockFactory = taskLockFactory;
		this.config = config;
	}

	public void run(long executionTime) {
		Lock lock = taskLockFactory.getLock(config, executionTime);
		try {
			if (lock == null || lock.tryLock()) {
				TaskListener taskListener = config.getTaskListener();
				try {
					logger.debug("开始执行 [{}]", config.getTaskId());
					if (taskListener != null) {
						taskListener.begin(config, executionTime);
					}
					config.getTask().run(executionTime);
					logger.debug("执行[{}]成功", config.getTaskId());
					if (taskListener != null) {
						taskListener.success(config, executionTime);
					}
				} catch (Throwable e) {
					logger.error(e, "执行[{}]异常", config.getTaskId());
					if (taskListener != null) {
						taskListener.error(config, executionTime, e);
					}
				} finally {
					if (taskListener != null) {
						taskListener.complete(config, executionTime);
					}
				}
			}
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}
}
