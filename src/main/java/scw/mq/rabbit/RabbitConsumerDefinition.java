package scw.mq.rabbit;

import java.util.Map;

import com.rabbitmq.client.AMQP.BasicProperties;

public interface RabbitConsumerDefinition<T> {
	/**
	 * 队列名称
	 * 
	 * @return
	 */
	String getQueueName();

	/**
	 * 是否持久化
	 * 
	 * @return
	 */
	boolean isDurable();

	/**
	 * 是否排他性
	 * 
	 * @return
	 */
	boolean isExclusive();

	/**
	 * 是否自动删除(是否自动ack)
	 * 
	 * @return
	 */
	boolean isAutoDelete();

	Map<String, Object> getArguments();

	void consumer(BasicProperties properties, T message) throws Throwable;
}
