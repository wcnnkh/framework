package io.basc.framework.util.exchange;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.Receipt;

public interface Channel<T> extends Publisher<T> {
	/**
	 * Constant for sending a message without a prescribed timeout.
	 */
	long INDEFINITE_TIMEOUT = -1;

	@Override
	default Receipt publish(T resource) {
		return publish(resource, INDEFINITE_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	Receipt publish(T resource, long timeout, TimeUnit timeUnit);
}
