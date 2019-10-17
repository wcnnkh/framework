package scw.core;

import scw.core.annotation.Ignore;

/**
 * 生产者
 */
@Ignore
public interface Producer<T> {
	void push(T message);
}