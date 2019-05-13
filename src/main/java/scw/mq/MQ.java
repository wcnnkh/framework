package scw.mq;

public interface MQ<T> extends Producer<T>{

	/**
	 * 添加消费者
	 * @param message
	 */
	void addConsumer(Consumer<T> consumer);
}
