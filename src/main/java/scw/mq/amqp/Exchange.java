package scw.mq.amqp;

import java.util.Map;

import scw.core.Consumer;

public interface Exchange<T> {
	void bindConsumer(String routingKey, String queueName, boolean durable, boolean exclusive, boolean autoDelete,
			Map<String, Object> arguments, Consumer<T> consumer);

	void bindConsumer(String routingKey, String queueName, boolean durable, boolean exclusive, boolean autoDelete,
			Consumer<T> consumer);
	
	void bindConsumer(String routingKey, String queueName,
			Consumer<T> consumer);

	void push(String routingKey, boolean mandatory, boolean immediate, T message);

	void push(String routingKey, T message);
}
