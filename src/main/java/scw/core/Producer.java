package scw.core;

/**
 * 生产者
 */
public interface Producer<T> {
	void push(T message);
}