package io.basc.framework.amqp;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.basc.framework.util.Assert;
import io.basc.framework.util.actor.DelayableNamedEventDispatcher;
import io.basc.framework.util.actor.EventListener;
import io.basc.framework.util.actor.EventPushException;
import io.basc.framework.util.actor.EventRegistrationException;
import io.basc.framework.util.actor.broadcast.BroadcastDelayableNamedEventDispatcher;
import io.basc.framework.util.actor.unicast.UnicastDelayableNamedEventDispatcher;
import io.basc.framework.util.register.Registration;

public class AmqpNamedEventDispatcher<T> implements UnicastDelayableNamedEventDispatcher<String, T>,
		BroadcastDelayableNamedEventDispatcher<String, T>, DelayableNamedEventDispatcher<String, T> {
	private final Exchange<T> exchange;
	private final Function<String, ? extends QueueDeclare> queueDeclareCreator;

	public AmqpNamedEventDispatcher(Exchange<T> exchange,
			Function<String, ? extends QueueDeclare> queueDeclareCreator) {
		Assert.requiredArgument(exchange != null, "exchange");
		Assert.requiredArgument(queueDeclareCreator != null, "queueDeclareCreator");
		this.exchange = exchange;
		this.queueDeclareCreator = queueDeclareCreator;
	}

	@Override
	public void publishEvent(String name, T event) throws EventPushException {
		exchange.push(name, new Message<T>(event));
	}

	@Override
	public Registration registerListener(String name, EventListener<T> eventListener)
			throws EventRegistrationException {
		return exchange.bind(name, queueDeclareCreator.apply(name), (a, b, c) -> eventListener.onEvent(c.getBody()));
	}

	@Override
	public void publishEvent(String name, T event, long delay, TimeUnit delayTimeUnit) throws EventPushException {
		Message<T> message = new Message<T>(event);
		message.setDelay(delay, delayTimeUnit);
		exchange.push(name, message);
	}

}
