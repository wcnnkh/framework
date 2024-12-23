package io.basc.framework.jms.boot.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.jms.boot.JmsSupplier;
import io.basc.framework.util.comparator.Ordered;

@ConditionalOnParameters(order = Ordered.LOWEST_PRECEDENCE)
public class DefaultJmsSupplier implements JmsSupplier {
	private Session session;

	public DefaultJmsSupplier(Session session) {
		this.session = session;
	}

	private Destination createDestination(AnnotatedElement element) throws JMSException {
		DestinationTopic destinationTopic = element.getAnnotation(DestinationTopic.class);
		if (destinationTopic != null) {
			return session.createTopic(destinationTopic.value());
		}

		DestinationQueue destinationQueue = element.getAnnotation(DestinationQueue.class);
		if (destinationQueue != null) {
			return session.createQueue(destinationQueue.value());
		}
		return null;
	}

	private String getMessageSelector(AnnotatedElement element) {
		MessageSelector messageSelector = element.getAnnotation(MessageSelector.class);
		if (messageSelector == null) {
			return null;
		}

		return messageSelector.value();
	}

	@Override
	public <T> MessageConsumer getMessageConsumer(Class<? extends T> clazz) throws JMSException {
		Destination destination = createDestination(clazz);
		if (destination == null) {
			return null;
		}

		return session.createConsumer(destination, getMessageSelector(clazz));
	}

	@Override
	public MessageConsumer getMessageConsumer(Class<?> clazz, Method method) throws JMSException {
		Destination destination = createDestination(method);
		if (destination == null) {
			destination = createDestination(clazz);
		}

		if (destination == null) {
			return null;
		}

		return session.createConsumer(destination, getMessageSelector(method));
	}

}
