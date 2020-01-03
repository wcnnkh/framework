package scw.mq.queue;

import scw.core.Consumer;

/**
 * 队列
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Queue<E> {
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
