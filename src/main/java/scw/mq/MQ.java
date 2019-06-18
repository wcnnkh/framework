package scw.mq;

import scw.core.Consumer;

public interface MQ<T>{

	void push(String name, T message);
	
	/**
	 * 添加消费者
	 * 
	 * @param message
	 */
	void bindConsumer(String name, Consumer<T> consumer);
}
