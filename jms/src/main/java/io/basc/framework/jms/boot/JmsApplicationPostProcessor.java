package io.basc.framework.jms.boot;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.NameInstanceSupplier;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.core.Ordered;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.reflect.MethodInvoker;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.logging.LogManager;

@ConditionalOnParameters(order = Ordered.LOWEST_PRECEDENCE)
public class JmsApplicationPostProcessor implements ApplicationPostProcessor {
	private static Logger logger = LogManager.getLogger(JmsApplicationPostProcessor.class);

	private List<JmsSupplier> getJmsResolvers(BeanFactory beanFactory) {
		List<JmsSupplier> resolvers = beanFactory.getServiceLoader(JmsSupplier.class).toList();
		if (!CollectionUtils.isEmpty(resolvers)) {
			return resolvers;
		}

		if (beanFactory.isInstance(JmsSupplier.class)) {
			return Arrays.asList(beanFactory.getInstance(JmsSupplier.class));
		}

		return beanFactory.getBeans(JmsSupplier.class).entrySet().stream().map((e) -> e.getValue())
				.collect(Collectors.toList());
	}

	@Override
	public void postProcessApplication(ConfigurableApplication application) throws Throwable {
		List<JmsSupplier> jmsResolvers = getJmsResolvers(application);
		if (CollectionUtils.isEmpty(jmsResolvers)) {
			return;
		}

		for (Class<?> clazz : application.getContextClasses()) {
			for (JmsSupplier jmsResolver : jmsResolvers) {
				MessageConsumer messageConsumer = jmsResolver.getMessageConsumer(clazz);
				if (messageConsumer != null) {
					if (!application.isInstance(clazz)) {
						logger.error("Cannot create instance {}", clazz);
					}

					MessageListener messageListener = jmsResolver.getMessageListener(clazz,
							application.getInstance(clazz));
					messageConsumer.setMessageListener(messageListener);
					logger.info("add class [{}] message listener {}", clazz, messageListener);
				}

				for (Method method : clazz.getDeclaredMethods()) {
					messageConsumer = jmsResolver.getMessageConsumer(clazz, method);
					if (messageConsumer == null) {
						continue;
					}

					Supplier<Object> supplier = new NameInstanceSupplier<Object>(application, clazz.getName());
					MethodInvoker methodInvoker = application.getAop().getProxyMethod(clazz, supplier, method);
					MessageListener listener = jmsResolver.getMessageListener(clazz, method, methodInvoker);
					logger.info("add method [{}] message listener {}", method, listener);
					messageConsumer.setMessageListener(listener);
				}
			}
		}
	}
}
