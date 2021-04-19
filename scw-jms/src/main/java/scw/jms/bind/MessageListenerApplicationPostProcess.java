package scw.jms.bind;

import java.lang.reflect.Method;

import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import scw.boot.ApplicationPostProcessor;
import scw.boot.ConfigurableApplication;
import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.core.reflect.MethodInvoker;
import scw.instance.supplier.NameInstanceSupplier;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class MessageListenerApplicationPostProcess implements ApplicationPostProcessor {
	private static Logger logger = LoggerFactory.getLogger(MessageListenerApplicationPostProcess.class);

	@Override
	public void postProcessApplication(ConfigurableApplication application) throws Throwable {
		if (!application.getBeanFactory().isInstance(MessageConsumerFactory.class)) {
			return;
		}

		if (!application.getBeanFactory().isInstance(MessageListenerFactory.class)) {
			return;
		}

		MessageConsumerFactory factory = application.getBeanFactory().getInstance(MessageConsumerFactory.class);
		MessageListenerFactory listenerFactory = application.getBeanFactory().getInstance(MessageListenerFactory.class);
		for (Class<?> clazz : application.getContextClassesLoader()) {
			scw.jms.bind.MessageListener messageListener = clazz
					.getAnnotation(scw.jms.bind.MessageListener.class);
			if (messageListener != null) {
				if (!MessageListener.class.isAssignableFrom(clazz)) {
					logger.error("{} non assignable {}", clazz, javax.jms.MessageListener.class);
				} else {
					if (!application.getBeanFactory().isInstance(clazz)) {
						logger.error("Cannot create instance {}", clazz);
					} else {
						MessageListener listener = (MessageListener) application.getBeanFactory().getInstance(clazz);
						MessageConsumer messageConsumer = factory.getMessageConsumer(clazz);
						messageConsumer.setMessageListener(listener);
						logger.info("add class [{}] message listener {}", clazz, listener);
					}
				}
			}

			for (Method method : clazz.getDeclaredMethods()) {
				messageListener = method.getAnnotation(scw.jms.bind.MessageListener.class);
				if (messageListener != null) {
					scw.util.Supplier<Object> supplier = new NameInstanceSupplier<Object>(application.getBeanFactory(),
							clazz.getName());
					MethodInvoker methodInvoker = application.getBeanFactory().getAop().getProxyMethod(clazz, supplier,
							method);

					MessageListener listener = listenerFactory.getMessageListener(methodInvoker);
					MessageConsumer messageConsumer = factory.getMessageConsumer(method);
					messageConsumer.setMessageListener(listener);
					logger.info("add method [{}] message listener {}", method, listener);
				}
			}
		}
	}
}
