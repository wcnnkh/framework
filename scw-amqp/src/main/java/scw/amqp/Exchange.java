package scw.amqp;

import scw.core.reflect.MethodInvoker;


public interface Exchange {
	void bind(String routingKey, QueueDeclare queueDeclare, MessageListener messageListener) throws ExchangeException;

	void push(String routingKey, Message message) throws ExchangeException;

	void push(String routingKey, MessageProperties messageProperties, byte[] body) throws ExchangeException;

	void bind(String routingKey, QueueDeclare queueDeclare, MethodInvoker invoker) throws ExchangeException;

	void push(String routingKey, MethodMessage message) throws ExchangeException;
}
