package scw.amqp.support;

import scw.amqp.Exchange;
import scw.amqp.QueueDeclare;
import scw.event.Event;
import scw.event.EventDispatcher;
import scw.event.support.SimpleNamedEventDispatcher;

public class AmqpNamedEventDispatcher<T extends Event> extends SimpleNamedEventDispatcher<String, T> {
	private Exchange exchange;

	public AmqpNamedEventDispatcher(Exchange exchange) {
		super(true);
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
