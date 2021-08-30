package io.basc.framework.amqp.annotation;

import io.basc.framework.amqp.Exchange;
import io.basc.framework.amqp.QueueDeclare;
import io.basc.framework.annotation.AnnotationUtils;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.instance.supplier.NameInstanceSupplier;
import io.basc.framework.reflect.MethodInvoker;
import io.basc.framework.util.Supplier;

import java.lang.reflect.Method;

@Provider
public final class MessageListenerApplicationInitializer implements ApplicationPostProcessor {

	@SuppressWarnings("unchecked")
	public void postProcessApplication(ConfigurableApplication application)
			throws Throwable {
		for (Class<?> clazz : application.getContextClasses()) {
			if (io.basc.framework.amqp.MessageListener.class.isAssignableFrom(clazz)) {
				MessageListener messageListener = clazz.getAnnotation(MessageListener.class);
				if (messageListener != null) {
					Exchange exchange = application.getBeanFactory().getInstance(messageListener.exchange());
					io.basc.framework.amqp.MessageListener listener = application.getBeanFactory()
							.getInstance((Class<io.basc.framework.amqp.MessageListener>) clazz);
					exchange.bind(messageListener.routingKey(), createQueueDeclare(messageListener), listener);
				}
			}

			for (Method method : AnnotationUtils.getAnnoationMethods(clazz, true, true, MessageListener.class)) {
				MessageListener messageListener = method.getAnnotation(MessageListener.class);
				Exchange exchange = application.getBeanFactory().getInstance(messageListener.exchange());
				Supplier<Object> supplier = new NameInstanceSupplier<Object>(application.getBeanFactory(), clazz.getName());
				MethodInvoker invoker = application.getBeanFactory().getAop().getProxyMethod(clazz, supplier, method);
				exchange.bind(messageListener.routingKey(), createQueueDeclare(messageListener), invoker);
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
