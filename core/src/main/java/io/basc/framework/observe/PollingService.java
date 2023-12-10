package io.basc.framework.observe;

import java.util.concurrent.TimeUnit;

public interface PollingService<T> {
	T poll();

	T poll(long timeout, TimeUnit unit) throws InterruptedException;

	T take() throws InterruptedException;
}
