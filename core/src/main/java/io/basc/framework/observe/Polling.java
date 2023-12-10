package io.basc.framework.observe;

import java.util.concurrent.TimeUnit;

public interface Polling extends Runnable {
	void await() throws InterruptedException;

	boolean await(long timeout, TimeUnit unit) throws InterruptedException;
}
