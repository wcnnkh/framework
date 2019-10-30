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
		this.casOperations = memcached.getCASOperations();
	}

	public CASTaskLockFactory(Redis redis) {
		this.casOperations = redis.getCASOperations();
	}

	public Lock getLock(TaskConfig taskConfig, long executionTime) {
		return new CASTaskLock(casOperations, taskConfig.getTaskId(), executionTime);
	}

}
