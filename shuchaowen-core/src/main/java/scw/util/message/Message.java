package scw.util.message;

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

	/**
	 * Return the number of bytes contained in the message.
	 */
	int getPayloadLength();
}
