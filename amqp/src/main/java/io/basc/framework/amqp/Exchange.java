package io.basc.framework.amqp;

import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.lang.Nullable;

public interface Exchange {

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

	ArgsMessageCodec getMessageCodec();

	default void bind(String routingKey, QueueDeclare queueDeclare,
			MethodInvoker invoker) throws ExchangeException {
		bind(routingKey, queueDeclare, new MethodInvokerMessageListener(
				invoker, getMessageCodec()));
	}

	default void push(String routingKey, Object... args)
			throws ExchangeException {
		push(routingKey, null, args);
	}

	default void push(String routingKey,
			@Nullable MessageProperties messageProperties, Object... args)
			throws ExchangeException {
		byte[] body = getMessageCodec().encode(args);
		push(routingKey, messageProperties == null ? new MessageProperties()
				: messageProperties, body);
	}
}
