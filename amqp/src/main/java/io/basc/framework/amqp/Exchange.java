package io.basc.framework.amqp;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.Registration;

public interface Exchange<T> {
	Registration bind(String routingKey, QueueDeclare queueDeclare, MessageListener<T> messageListener)
			throws ExchangeException;

	void push(String routingKey, Message<T> message) throws ExchangeException;

	default <R> Exchange<R> convert(Codec<Message<T>, Message<R>> codec) {
		return new ConvertibleExchange<>(this, codec);
	}
}
