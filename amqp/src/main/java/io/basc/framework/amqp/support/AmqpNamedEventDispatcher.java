package io.basc.framework.amqp.support;

import io.basc.framework.amqp.Exchange;
import io.basc.framework.amqp.QueueDeclare;
import io.basc.framework.event.Event;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.support.SimpleNamedEventDispatcher;

public class AmqpNamedEventDispatcher<T extends Event> extends SimpleNamedEventDispatcher<String, T> {
	private Exchange exchange;

	public AmqpNamedEventDispatcher(Exchange exchange) {
		this.exchange = exchange;
	}
	
	protected QueueDeclare createQueueDeclare(Object name){
		QueueDeclare queueDeclare = new QueueDeclare("named.event.dispathcher." + name);
		return queueDeclare;
	}

	@Override
	protected EventDispatcher<T> createEventDispatcher(String name) {
		return new AmqpEventDisabtcher<T>(exchange, name, createQueueDeclare(name));
	}
}
