package io.basc.framework.util.actor;

import java.util.concurrent.TimeUnit;

/**
 * 可拉取的
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Pollable<T> {
	/**
	 * Receive a object, blocking indefinitely if necessary.
	 * 
	 * @return the next available {@link Object} or {@code null} if interrupted
	 */
	T poll();

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