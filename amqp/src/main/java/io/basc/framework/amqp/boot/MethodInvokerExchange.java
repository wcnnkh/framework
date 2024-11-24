package io.basc.framework.amqp.boot;

import io.basc.framework.amqp.BinaryExchange;
import io.basc.framework.amqp.Exchange;
import io.basc.framework.amqp.ExchangeException;
import io.basc.framework.amqp.Message;
import io.basc.framework.amqp.MessageListener;
import io.basc.framework.amqp.QueueDeclare;
import io.basc.framework.core.convert.value.Values;
import io.basc.framework.util.Assert;
import io.basc.framework.util.reflect.MethodInvoker;
import io.basc.framework.util.register.Registration;

public class MethodInvokerExchange implements BinaryExchange {
	private final Exchange<byte[]> exchange;
	private final MethodMessageCodec<byte[]> messageCodec;

	public MethodInvokerExchange(Exchange<byte[]> exchange) {
		this(exchange, SerializerMethodMessageCodec.DEFAULT);
	}

	public MethodInvokerExchange(Exchange<byte[]> exchange, MethodMessageCodec<byte[]> messageCodec) {
		Assert.requiredArgument(exchange != null, "exchange");
		Assert.requiredArgument(messageCodec != null, "messageCodec");
		this.exchange = exchange;
		this.messageCodec = messageCodec;
	}

	public Registration registerInvoker(String routingKey, QueueDeclare queueDeclare, MethodInvoker invoker)
			throws ExchangeException {
		return bind(routingKey, queueDeclare, new MethodInvokerMessageListener<byte[]>(invoker, messageCodec));
	}

	public void invoke(String routingKey, Message<Values> args) throws ExchangeException {
		Message<byte[]> message = messageCodec.encode(args);
		push(routingKey, message);
	}

	public void invoke(String routingKey, Object... args) throws ExchangeException {
		invoke(routingKey, new Message<Values>(Values.of(args)));
	}

	@Override
	public Registration bind(String routingKey, QueueDeclare queueDeclare, MessageListener<byte[]> messageListener)
			throws ExchangeException {
		return exchange.bind(routingKey, queueDeclare, messageListener);
	}

	@Override
	public void push(String routingKey, Message<byte[]> message) throws ExchangeException {
		exchange.push(routingKey, message);
	}
}
