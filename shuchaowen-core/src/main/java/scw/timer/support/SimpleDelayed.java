package scw.timer.support;

import java.util.concurrent.TimeUnit;

import scw.timer.Delayed;

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
