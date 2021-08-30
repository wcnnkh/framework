package io.basc.framework.timer;

import java.util.concurrent.TimeUnit;

public interface Delayed {
	long getDelay(TimeUnit timeUnit);
}
