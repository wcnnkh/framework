package io.basc.framework.amqp;

import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.lang.Nullable;

public interface Exchange {

	/**
	 * 根据规则绑定消费者
	 * 
	 * @param routingKey
	 * @param queueDeclare
	 * @param messageListener
	 * @throws ExchangeException
	 */
	void bind(String routingKey, QueueDeclare queueDeclare,
			MessageListener messageListener) throws ExchangeException;

	default void push(String routingKey, Message message)
			throws ExchangeException {
		push(routingKey, message, message.getBody());
	}

	default void push(String routingKey, byte[] body) throws ExchangeException {
		push(routingKey, new MessageProperties(), body);
	}

	void push(String routingKey, MessageProperties messageProperties,
			byte[] body) throws ExchangeException;

	/**
	 * @see #push(String, Object...)
	 * @see #push(String, MessageProperties, Object...)
	 * @see #bind(String, QueueDeclare, MethodInvoker)
	 * @return
	 */
	ArgsMessageCodec getMessageCodec();

	/**
	 * @see #push(String, MessageProperties, Object...)
	 * @param routingKey
	 * @param queueDeclare
	 * @param invoker
	 * @throws ExchangeException
	 */
	default void bind(String routingKey, QueueDeclare queueDeclare,
			MethodInvoker invoker) throws ExchangeException {
		bind(routingKey, queueDeclare, new MethodInvokerMessageListener(
				invoker, getMessageCodec()));
	}

	/**
	 * @see #bind(String, QueueDeclare, MethodInvoker)
	 * @param routingKey
	 * @param args
	 * @throws ExchangeException
	 */
	default void push(String routingKey, Object... args)
			throws ExchangeException {
		push(routingKey, null, args);
	}

	/**
	 * @see #bind(String, QueueDeclare, MethodInvoker)
	 * @param routingKey
	 * @param messageProperties
	 * @param args
	 * @throws ExchangeException
	 */
	default void push(String routingKey,
			@Nullable MessageProperties messageProperties, Object... args)
			throws ExchangeException {
		byte[] body = getMessageCodec().encode(args);
		push(routingKey, messageProperties == null ? new MessageProperties()
				: messageProperties, body);
	}
}
