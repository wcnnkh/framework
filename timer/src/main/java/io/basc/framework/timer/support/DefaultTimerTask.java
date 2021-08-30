package io.basc.framework.timer.support;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.timer.TaskConfig;
import io.basc.framework.timer.TaskListener;
import io.basc.framework.timer.TaskLockFactory;

import java.util.concurrent.locks.Lock;

public class DefaultTimerTask implements io.basc.framework.timer.Task {
	private static Logger logger = LoggerFactory.getLogger(DefaultTimerTask.class);
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
