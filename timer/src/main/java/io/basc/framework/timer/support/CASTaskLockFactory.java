package io.basc.framework.timer.support;

import java.util.concurrent.locks.Lock;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.data.StorageCasOperations;
import io.basc.framework.timer.TaskConfig;
import io.basc.framework.timer.TaskLockFactory;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class CASTaskLockFactory implements TaskLockFactory {
	private StorageCasOperations casOperations;

	public CASTaskLockFactory(StorageCasOperations casOperations) {
		this.casOperations = casOperations;
	}

	public Lock getLock(TaskConfig taskConfig, long executionTime) {
		return new CASTaskLock(casOperations, taskConfig.getTaskId(), executionTime);
	}

}
