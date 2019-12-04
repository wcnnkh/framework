package scw.core;

import scw.lang.Ignore;

/**
 * 消费者
 * @author shuchaowen
 *
 * @param <T>
 */

@Ignore
public interface Consumer<T> {
	/**
	 * 消费
	 * @param message
	 */
	void consume(T message) throws Throwable;
}
