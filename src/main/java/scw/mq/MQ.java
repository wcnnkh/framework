package scw.mq;

public interface MQ<T>{
	/**
	 * 推送一条消息
	 * @param message
	 */
	void push(T message);
	
	/**
	 * 添加一个消费者
	 * @param consumer
	 */
	void consumer(Consumer<T> consumer);
	
	void start();
	
	/**
	 * 销毁此队列
	 */
	void destroy();
}
