package scw.security.limit;

import java.util.concurrent.TimeUnit;

import scw.data.DataTemplete;
import scw.data.TemporaryCounter;

public final class DefaultCountLimitFactory implements CountLimitFactory {
	private final TemporaryCounter temporaryCounter;

	public DefaultCountLimitFactory(DataTemplete dataTemplete) {
		this.temporaryCounter = dataTemplete;
	}

	public DefaultCountLimitFactory(TemporaryCounter temporaryCounter) {
		this.temporaryCounter = temporaryCounter;
	}

	public long incrAndGet(String name, long timeout, TimeUnit timeUnit) {
		return temporaryCounter.incr(name, 1, 1, (int) timeUnit.toSeconds(timeout));
	}
}
