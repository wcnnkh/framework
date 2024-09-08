package io.basc.framework.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import io.basc.framework.util.Assert;
import io.basc.framework.util.function.Processor;
import io.basc.framework.util.function.Source;
import io.basc.framework.util.observe.RegistrationException;
import io.basc.framework.util.register.Registration;

public class SessionOperations<T extends Session, D extends Destination>
		extends AbstractJmsOperations<T, SessionOperations<T, D>> {

	private volatile D destination;

	private volatile MessageProducer messageProducer;

	private final Processor<? super T, ? extends D, ? extends JMSException> processor;

	private volatile T session;

	public SessionOperations(Source<? extends T, ? extends JMSException> source,
			Processor<? super T, ? extends D, ? extends JMSException> processor) {
		super(source);
		Assert.requiredArgument(processor != null, "processor");
		this.processor = processor;
	}

	public Registration bind(MessageListener messageListener) throws JMSException {
		return bind(getDefaultMessageSelector(), messageListener);
	}

	@Override
	public Registration bind(String messageSelector, MessageListener messageListener) throws JMSException {
		MessageConsumer consumer = createConsumer(messageSelector);
		consumer.setMessageListener(messageListener);
		return () -> {
			try {
				consumer.close();
			} catch (JMSException e) {
				throw new RegistrationException(e);
			}
		};
	}

	@Override
	public void close() throws JMSException {
		try {
			super.close();
		} finally {
			if (session != null) {
				synchronized (this) {
					if (session != null) {
						try {
							close(session);
						} finally {
							session = null;
						}
					}
				}
			}
		}
	}

	public MessageConsumer createConsumer(String messageSelector) throws JMSException {
		return createConsumer(get(), getDestination(), messageSelector, getNoLocal());
	}

	public MessageConsumer createConsumer(T session, D destination, String messageSelector, Boolean noLocal)
			throws JMSException {
		MessageConsumer consumer;
		if (messageSelector == null) {
			consumer = session.createConsumer(destination);
		} else {
			if (noLocal == null) {
				consumer = session.createConsumer(destination, messageSelector);
			} else {
				consumer = session.createConsumer(destination, messageSelector, noLocal);
			}
		}
		return consumer;
	}

	public D createDestination(T session) throws JMSException {
		return processor.process(session);
	}

	public MessageProducer createProducer(T session, D destination) throws JMSException {
		return session.createProducer(destination);
	}

	@Override
	public final T get() throws JMSException {
		if (session == null) {
			synchronized (this) {
				if (session == null) {
					session = super.get();
				}
			}
		}
		return session;
	}

	public final D getDestination() throws JMSException {
		if (destination == null) {
			synchronized (this) {
				if (destination == null) {
					destination = createDestination(get());
				}
			}
		}
		return destination;
	}

	public final MessageProducer getProducer() throws JMSException {
		if (messageProducer == null) {
			synchronized (this) {
				if (messageProducer == null) {
					messageProducer = createProducer(get(), getDestination());
				}
			}
		}
		return this.messageProducer;
	}

	@Override
	public void send(MessageBuilder messageBuilder) throws JMSException {
		MessageProducer producer = getProducer();
		Message message = messageBuilder.build(get());
		producer.send(message);
	}
}
