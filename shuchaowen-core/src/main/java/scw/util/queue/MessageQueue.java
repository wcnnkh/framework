package scw.util.queue;

/**
 * 消息队列
 * @author shuchaowen
 *
 * @param <T>
 */
public interface MessageQueue<E> extends Producer<E>{
	/**
	 * 向队列中推送消息
	 * 
	 * @param message
	 */
	void push(E message);

	/**
	 * 添加消费者,如果存在多个消费者会都消费一次
	 * 
	 * @param consumer
	 */
	void addConsumer(Consumer<E> consumer);
}
