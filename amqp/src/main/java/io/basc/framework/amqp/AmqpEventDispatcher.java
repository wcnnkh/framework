package io.basc.framework.amqp;

import java.util.concurrent.TimeUnit;

import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventPushException;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.broadcast.BroadcastDelayableEventDispatcher;
import io.basc.framework.event.unicast.UnicastDelayableEventDispatcher;
import io.basc.framework.util.Assert;
import io.basc.framework.util.register.Registration;

public class AmqpEventDispatcher<T>
		implements UnicastDelayableEventDispatcher<T>, BroadcastDelayableEventDispatcher<T>, EventDispatcher<T> {
	private final Exchange<T> exchange;
	private final QueueDeclare queueDeclare;
	private final String routingKey;

	public AmqpEventDispatcher(Exchange<T> exchange, String routingKey, QueueDeclare queueDeclare) {
		Assert.requiredArgument(exchange != null, "exchange");
		Assert.requiredArgument(routingKey != null, "routingKey");
		Assert.requiredArgument(queueDeclare != null, "queueDeclare");
		this.exchange = exchange;
		this.routingKey = routingKey;
		this.queueDeclare = queueDeclare;
	}

	public Exchange<T> getExchange() {
		return exchange;
	}

	public QueueDeclare getQueueDeclare() {
		return queueDeclare;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	@Override
	public void publishEvent(T event) throws EventPushException {
		exchange.push(routingKey, new Message<T>(event));
	}

	@Override
	public void publishEvent(T event, long delay, TimeUnit delayTimeUnit) throws EventPushException {
		Message<T> message = new Message<T>(event);
		message.setDelay(delay, delayTimeUnit);
		exchange.push(routingKey, message);
	}

	@Override
	public Registration registerListener(EventListener<T> eventListener) throws EventRegistrationException {
		return exchange.bind(routingKey, queueDeclare, (a, b, c) -> eventListener.onEvent(c.getBody()));
	}
}
