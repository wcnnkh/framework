package scw.amqp;

import scw.aop.MethodInvoker;
import scw.beans.annotation.Bean;

@Bean(proxy = false)
public interface Exchange {
	void bind(String routingKey, QueueDeclare queueDeclare, MessageListener messageListener);

	void push(String routingKey, Message message);

	void push(String routingKey, MessageProperties messageProperties, byte[] body);

	void bind(String routingKey, QueueDeclare queueDeclare, MethodInvoker invoker);

	void push(String routingKey, MethodMessage message);

	/**
	 * @param routingKey
	 * @param message
	 * @param transaction 指的是本地事务，并非rabbitmq transaction
	 */
	void push(String routingKey, Message message, boolean transaction);

	/**
	 * 
	 * @param routingKey
	 * @param message
	 * @param transaction 指的是本地事务，并非rabbitmq transaction
	 */
	void push(String routingKey, MethodMessage message, boolean transaction);

	/**
	 * 
	 * @param routingKey
	 * @param messageProperties
	 * @param body
	 * @param transaction 指的是本地事务，并非rabbitmq transaction
	 */
	void push(String routingKey, MessageProperties messageProperties, byte[] body, boolean transaction);
}
