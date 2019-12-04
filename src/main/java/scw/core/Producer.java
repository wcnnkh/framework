package scw.core;

import scw.lang.Ignore;

/**
 * 生产者
 */
@Ignore
public interface Producer<T> {
	void push(T message);
}