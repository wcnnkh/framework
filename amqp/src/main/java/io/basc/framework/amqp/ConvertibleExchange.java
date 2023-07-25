package io.basc.framework.amqp;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.Assert;
import io.basc.framework.util.registry.Registration;

public class ConvertibleExchange<S, T> implements Exchange<T> {
	private final Exchange<S> sourceExchange;
	private final Codec<Message<S>, Message<T>> codec;

	public ConvertibleExchange(Exchange<S> sourceExchange, Codec<Message<S>, Message<T>> codec) {
		Assert.requiredArgument(sourceExchange != null, "sourceExchange");
		Assert.requiredArgument(codec != null, "codec");
		this.sourceExchange = sourceExchange;
		this.codec = codec;
	}

	@Override
	public Registration bind(String routingKey, QueueDeclare queueDeclare, MessageListener<T> messageListener)
			throws ExchangeException {
		return sourceExchange.bind(routingKey, queueDeclare,
				(a, b, c) -> messageListener.onMessage(a, b, codec.encode(c)));
	}

	@Override
	public void push(String routingKey, Message<T> message) throws ExchangeException {
		sourceExchange.push(routingKey, codec.decode(message));
	}

}
