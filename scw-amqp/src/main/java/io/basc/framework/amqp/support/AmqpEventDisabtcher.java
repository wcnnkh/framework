package io.basc.framework.amqp.support;

import io.basc.framework.amqp.Exchange;
import io.basc.framework.amqp.Message;
import io.basc.framework.amqp.MessageListener;
import io.basc.framework.amqp.QueueDeclare;
import io.basc.framework.event.Event;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.event.support.SimpleEventDispatcher;
import io.basc.framework.io.Serializer;
import io.basc.framework.io.SerializerUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class AmqpEventDisabtcher<T extends Event> extends SimpleEventDispatcher<T>
		implements MessageListener {
	private Exchange exchange;
	private String routingKey;
	private QueueDeclare queueDeclare;
	private Serializer serializer = SerializerUtils.getSerializer();

	public AmqpEventDisabtcher(Exchange exchange, String routingKey, QueueDeclare queueDeclare) {
		super(true);
		this.exchange = exchange;
		this.queueDeclare = queueDeclare;
	}

	public Serializer getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	private volatile AtomicBoolean bind = new AtomicBoolean(false);

	@Override
	public EventRegistration registerListener(EventListener<T> eventListener) {
		if (!bind.get() && bind.compareAndSet(false, true)) {
			exchange.bind(routingKey, queueDeclare, this);
		}
		return super.registerListener(eventListener);
	}

	public void onMessage(String exchange, String routingKey, Message message) throws IOException {
		try {
			T event = getSerializer().deserialize(message.getBody());
			publishEvent(event);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
