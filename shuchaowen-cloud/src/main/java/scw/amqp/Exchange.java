package scw.amqp;

import scw.aop.MethodInvoker;

public interface Exchange {
	void bind(String routingKey, QueueDeclare queueDeclare, MessageListener messageListener);

	void push(String routingKey, Message message);

	void bind(String routingKey, QueueDeclare queueDeclare, MethodInvoker methodInvoker);

	void push(String routingKey, MethodMessage message);
}
