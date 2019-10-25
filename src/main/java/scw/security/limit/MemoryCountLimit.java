package scw.security.limit;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class MemoryCountLimit implements CountLimit {
	private final AtomicLong count;
	private final AtomicLong lastTimeout;
	private volatile CountLimitConfig countLimitConfig;

	public MemoryCountLimit() {
		count = new AtomicLong(0);
		this.lastTimeout = new AtomicLong(System.currentTimeMillis());
	}

	public void setConfig(CountLimitConfig countLimitConfig) {
		this.countLimitConfig = countLimitConfig;
	}

	public boolean incr() {
		long period = countLimitConfig.getPeriod(TimeUnit.MILLISECONDS);
		if (period > 0 && System.currentTimeMillis() - lastTimeout.get() >= period) {
			count.set(1);
			lastTimeout.set(System.currentTimeMillis());
			return true;
		}

		return count.incrementAndGet() <= countLimitConfig.getMaxCount();
	}

	public void reset() {
		count.set(0);
		lastTimeout.set(System.currentTimeMillis());
	}

	public long getCount() {
		return count.get();
	}
}
