package scw.security.limit;

import java.util.concurrent.TimeUnit;

public final class SimpleCountLimitConfig implements CountLimitConfig {
	private final String name;
	private final long maxCount;
	private final long period;
	private final TimeUnit timeUnit;

	public SimpleCountLimitConfig(String name, long maxCount, long period, TimeUnit timeUnit) {
		this.name = name;
		this.maxCount = maxCount;
		this.period = period;
		this.timeUnit = timeUnit;
	}

	public String getName() {
		return name;
	}

	public long getMaxCount() {
		return maxCount;
	}

	public long getTimeout() {
		return period;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

}
