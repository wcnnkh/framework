package io.basc.framework.data.generator.counter;

import io.basc.framework.data.Counter;
import io.basc.framework.data.generator.IdGenerator;

public final class CounterIdGenerator implements IdGenerator<Long> {
	private final Counter counter;
	private final String key;
	private final long initId;

	public CounterIdGenerator(Counter counter, String key, long initId) {
		this.key = key;
		this.counter = counter;
		this.initId = initId;
	}

	public Long next() {
		return counter.incr(key, 1, initId);
	}
}
