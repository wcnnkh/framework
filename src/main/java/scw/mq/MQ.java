package scw.mq;

public interface MQ<T> {
	/**
	 * 推送数据
	 * @param message
	 */
	void push(T message);

	/**
	 * 添加消费者
	 * @param message
	 */
	void addConsumer(Consumer<T> consumer);
}
