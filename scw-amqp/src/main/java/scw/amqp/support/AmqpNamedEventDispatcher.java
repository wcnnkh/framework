package scw.amqp.support;

import scw.amqp.Exchange;
import scw.amqp.QueueDeclare;
import scw.event.EventDispatcher;
import scw.event.Event;
import scw.event.support.DefaultNamedEventDispatcher;

public class AmqpNamedEventDispatcher<T extends Event> extends DefaultNamedEventDispatcher<String, T> {
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
	protected EventDispatcher<T> createBasicEventDispatcher(String name) {
		return new AmqpBasicEventDisabtcher<T>(exchange, name, createQueueDeclare(name));
	}
}
