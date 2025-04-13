package run.soeasy.framework.core.exchange;

import java.util.concurrent.TimeUnit;

public interface PollableChannel<T> extends Pollable<T>, Channel<T> {
	@Override
	default T poll() {
		return poll(INDEFINITE_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	/**
	 * Receive a object from this channel, blocking until either a message is
	 * available or the specified timeout period elapses.
	 * 
	 * @param timeout
	 * @param timeUnit
	 * @return
	 */
	T poll(long timeout, TimeUnit timeUnit);
}
