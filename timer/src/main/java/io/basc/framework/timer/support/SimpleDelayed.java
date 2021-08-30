package io.basc.framework.timer.support;

import io.basc.framework.timer.Delayed;

import java.util.concurrent.TimeUnit;

public final class SimpleDelayed implements Delayed {
	private final long delay;
	private final TimeUnit timeUnit;

	public SimpleDelayed(long delay, TimeUnit timeUnit) {
		this.delay = delay;
		this.timeUnit = timeUnit;
	}

	public long getDelay(TimeUnit timeUnit) {
		return timeUnit.convert(delay, this.timeUnit);
	}

}
