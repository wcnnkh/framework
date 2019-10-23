package scw.timer.support;

import scw.data.cas.CASOperations;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.locks.Lock;
import scw.timer.TaskConfig;
import scw.timer.TaskLockFactory;

public class CASTaskLockFactory implements TaskLockFactory {
	private CASOperations casOperations;

	public CASTaskLockFactory(Memcached memcached) {
		this(memcached.getCASOperations());
	}

	public CASTaskLockFactory(Redis redis) {
		this(redis.getCASOperations());
	}

	public CASTaskLockFactory(CASOperations casOperations) {
		this.casOperations = casOperations;
	}

	public Lock getLock(TaskConfig taskConfig, long executionTime) {
		return new CASTaskLock(casOperations, taskConfig.getTaskId(), executionTime);
	}

}
