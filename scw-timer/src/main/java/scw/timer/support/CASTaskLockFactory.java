package scw.timer.support;

import java.util.concurrent.locks.Lock;

import scw.context.annotation.Provider;
import scw.data.cas.CASOperations;
import scw.memcached.Memcached;
import scw.redis.core.Redis;
import scw.timer.TaskConfig;
import scw.timer.TaskLockFactory;

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
