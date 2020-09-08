package scw.amqp.support;

import scw.amqp.Exchange;
import scw.amqp.QueueDeclare;
import scw.event.BasicEventDispatcher;
import scw.event.Event;
import scw.event.support.DefaultNamedEventDispatcher;

public class AmpqNamedEventDispatcher<T extends Event> extends DefaultNamedEventDispatcher<T> {
	private Exchange exchange;
	private QueueDeclare queueDeclare;

	public AmpqNamedEventDispatcher(Exchange exchange, QueueDeclare queueDeclare, boolean concurrent) {
		super(concurrent);
		this.exchange = exchange;
		this.queueDeclare = queueDeclare;
	}

	@Override
	protected BasicEventDispatcher<T> create(Object name) {
		return new AmqpBasicEventDisabtcher<T>(exchange, name.toString(), queueDeclare, isConcurrent());
	}
}
