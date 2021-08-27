package io.basc.framework.timer.support;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.data.cas.CASOperations;
import io.basc.framework.memcached.Memcached;
import io.basc.framework.redis.core.Redis;
import io.basc.framework.timer.TaskConfig;
import io.basc.framework.timer.TaskLockFactory;

import java.util.concurrent.locks.Lock;

@Provider
public class CASTaskLockFactory implements TaskLockFactory {
	private CASOperations casOperations;

	public CASTaskLockFactory(Memcached memcached) {
		this.casOperations = memcached.getCASOperations();
	}

	public CASTaskLockFactory(Redis redis) {
		this.casOperations = redis.getCASOperations();
	}

	public Lock getLock(TaskConfig taskConfig, long executionTime) {
		return new CASTaskLock(casOperations, taskConfig.getTaskId(), executionTime);
	}

}
