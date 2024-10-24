package io.basc.framework.amqp.boot;

import java.util.function.Supplier;

import io.basc.framework.amqp.BinaryMessageListener;
import io.basc.framework.amqp.QueueDeclare;
import io.basc.framework.amqp.boot.annotation.MessageListener;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.factory.NameInstanceSupplier;

@Provider
public final class MessageListenerApplicationInitializer implements ApplicationPostProcessor {

	@SuppressWarnings("unchecked")
	public void postProcessApplication(ConfigurableApplication application) {
		for (Class<?> clazz : application.getContextClasses()) {
			if (io.basc.framework.amqp.MessageListener.class.isAssignableFrom(clazz)) {
				MessageListener messageListener = clazz.getAnnotation(MessageListener.class);
				if (messageListener != null) {
					MethodInvokerExchange exchange = application.getInstance(messageListener.exchange());
					BinaryMessageListener listener = application.getInstance((Class<BinaryMessageListener>) clazz);
					exchange.bind(messageListener.routingKey(), createQueueDeclare(messageListener), listener);
				}
			}

			ReflectionUtils.getDeclaredMethods(clazz).withAll().all().stream()
					.filter((e) -> e.isAnnotationPresent(MessageListener.class)).forEach((method) -> {
						MessageListener messageListener = method.getAnnotation(MessageListener.class);
						MethodInvokerExchange exchange = application.getInstance(messageListener.exchange());
						Supplier<Object> supplier = new NameInstanceSupplier<Object>(application, clazz.getName());
						MethodInvoker invoker = application.getAop().getProxyMethod(clazz, supplier, method);
						exchange.registerInvoker(messageListener.routingKey(), createQueueDeclare(messageListener),
								invoker);
					});
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
