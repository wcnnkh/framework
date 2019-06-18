package scw.application.crontab;

import scw.data.cas.CASOperations;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;

public final class DefaultCrontabContextFactory implements CrontabContextFactory {
	private final CASOperations casOperations;

	public DefaultCrontabContextFactory() {
		this.casOperations = null;
	};

	public DefaultCrontabContextFactory(CASOperations casOperations) {
		this.casOperations = casOperations;
	}

	public DefaultCrontabContextFactory(Memcached memcached) {
		this.casOperations = memcached.getCASOperations();
	}

	public DefaultCrontabContextFactory(Redis redis) {
		this.casOperations = redis.getCASOperations();
	}

	public CrontabContext getContext(String name, long executionTime) {
		return casOperations == null ? new DefaultCrontabContext()
				: new CASCrontabContext(casOperations, name, executionTime);
	}

}
