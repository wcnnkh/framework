package io.basc.framework.jms.bind;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import io.basc.framework.boot.ApplicationException;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.factory.NameInstanceSupplier;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class MessageListenerApplicationPostProcess implements ApplicationPostProcessor {
	private static Logger logger = LoggerFactory.getLogger(MessageListenerApplicationPostProcess.class);

	@Override
	public void postProcessApplication(ConfigurableApplication application) throws Throwable {
		if (!application.isInstance(MessageConsumerFactory.class)) {
			return;
		}

		if (!application.isInstance(MessageListenerFactory.class)) {
			return;
		}

		MessageConsumerFactory factory = application.getInstance(MessageConsumerFactory.class);
		MessageListenerFactory listenerFactory = application.getInstance(MessageListenerFactory.class);
		for (Class<?> clazz : application.getContextClasses()) {
			io.basc.framework.jms.bind.MessageListener messageListener = clazz
					.getAnnotation(io.basc.framework.jms.bind.MessageListener.class);
			if (messageListener != null) {
				if (!MessageListener.class.isAssignableFrom(clazz)) {
					logger.error("{} non assignable {}", clazz, javax.jms.MessageListener.class);
				} else {
					if (!application.isInstance(clazz)) {
						logger.error("Cannot create instance {}", clazz);
					} else {
						MessageListener listener = (MessageListener) application.getInstance(clazz);
						MessageConsumer messageConsumer = factory.getMessageConsumer(clazz);
						try {
							messageConsumer.setMessageListener(listener);
						} catch (JMSException e) {
							throw new ApplicationException(e);
						}
						logger.info("add class [{}] message listener {}", clazz, listener);
					}
				}
			}

			for (Method method : clazz.getDeclaredMethods()) {
				messageListener = method.getAnnotation(io.basc.framework.jms.bind.MessageListener.class);
				if (messageListener != null) {
					Supplier<Object> supplier = new NameInstanceSupplier<Object>(application, clazz.getName());
					MethodInvoker methodInvoker = application.getAop().getProxyMethod(clazz, supplier, method);

					MessageListener listener = listenerFactory.getMessageListener(methodInvoker);
					MessageConsumer messageConsumer = factory.getMessageConsumer(method);
					messageConsumer.setMessageListener(listener);
					logger.info("add method [{}] message listener {}", method, listener);
				}
			}
		}
	}
}
