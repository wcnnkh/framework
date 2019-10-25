package scw.security.limit;

import java.util.concurrent.TimeUnit;

public interface CountLimitConfig {
	String getName();

	long getMaxCount();

	long getPeriod(TimeUnit timeUnit);
}
