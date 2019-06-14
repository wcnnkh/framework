package scw.mq.amqp;

import java.util.Map;

import scw.core.Consumer;

public interface Exchange<T> {
	void addConsumer(String routingKey, String queueName, boolean durable, boolean exclusive, boolean autoDelete,
			Map<String, Object> arguments, Consumer<T> consumer);

	void addConsumer(String routingKey, String queueName, boolean durable, boolean exclusive, boolean autoDelete,
			Consumer<T> consumer);

	void push(String routingKey, boolean mandatory, boolean immediate, T message);

	void push(String routingKey, T message);
}
