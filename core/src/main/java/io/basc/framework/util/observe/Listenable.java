package io.basc.framework.util.observe;

import java.util.concurrent.TimeUnit;

/**
 * 可监听的
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Listenable<T> extends Observable<T> {
	/**
	 * Waits for this future to be completed within the specified time limit.
	 *
	 * @return {@code true} if and only if the future was completed within the
	 *         specified time limit
	 *
	 * @throws InterruptedException if the current thread was interrupted
	 */
	boolean await(long timeout, TimeUnit unit) throws InterruptedException;
}
