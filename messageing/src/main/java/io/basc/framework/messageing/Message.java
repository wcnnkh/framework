package io.basc.framework.messageing;

/**
 * 对于一个消息的定义
 *
 * @param <T>
 */
public interface Message<T> {
	/**
	 * Return the message payload (never {@code null}).
	 */
	T getPayload();

	MessageHeaders getHeaders();
}
