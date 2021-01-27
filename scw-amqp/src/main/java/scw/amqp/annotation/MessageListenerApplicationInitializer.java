package scw.amqp.annotation;

import java.lang.reflect.Method;

import scw.amqp.Exchange;
import scw.amqp.QueueDeclare;
import scw.boot.ApplicationPostProcessor;
import scw.boot.ConfigurableApplication;
import scw.context.annotation.Provider;
import scw.core.annotation.AnnotationUtils;

@Provider(order = Integer.MIN_VALUE)
public final class MessageListenerApplicationInitializer implements ApplicationPostProcessor {

	@SuppressWarnings("unchecked")
	public void postProcessApplication(ConfigurableApplication application)
			throws Throwable {
		for (Class<?> clazz : application.getContextClassesLoader()) {
			if (scw.amqp.MessageListener.class.isAssignableFrom(clazz)) {
				MessageListener messageListener = clazz.getAnnotation(MessageListener.class);
				if (messageListener != null) {
					Exchange exchange = application.getBeanFactory().getInstance(messageListener.exchange());
					scw.amqp.MessageListener listener = application.getBeanFactory()
							.getInstance((Class<scw.amqp.MessageListener>) clazz);
					exchange.bind(messageListener.routingKey(), createQueueDeclare(messageListener), listener);
				}
			}

			for (Method method : AnnotationUtils.getAnnoationMethods(clazz, true, true, MessageListener.class)) {
				MessageListener messageListener = method.getAnnotation(MessageListener.class);
				Exchange exchange = application.getBeanFactory().getInstance(messageListener.exchange());
				exchange.bind(messageListener.routingKey(), createQueueDeclare(messageListener), application
						.getBeanFactory().getAop().getProxyMethod(application.getBeanFactory(), clazz, method));
			}
		}
	}
	
	private QueueDeclare createQueueDeclare(MessageListener messageListener) {
		QueueDeclare queueDeclare = new QueueDeclare(messageListener.queueName());
		queueDeclare.setDurable(messageListener.durable());
		queueDeclare.setExclusive(messageListener.exclusive());
		queueDeclare.setAutoDelete(messageListener.autoDelete());
		return queueDeclare;
	}
}
