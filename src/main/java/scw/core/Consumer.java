package scw.core;

/**
 * 消费者
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Consumer<T> {
	/**
	 * 消费
	 * @param message
	 */
	void consume(T message) throws Exception;
}
