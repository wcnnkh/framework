package io.basc.framework.rabbitmq.beans;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConnectionFactoryConfigurator;

import io.basc.framework.amqp.Exchange;
import io.basc.framework.amqp.ExchangeDeclare;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.support.BeanDefinitionLoader;
import io.basc.framework.beans.factory.support.BeanDefinitionLoaderChain;
import io.basc.framework.beans.factory.support.FactoryBeanDefinition;
import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.context.support.ContextBeanDefinition;
import io.basc.framework.context.support.ContextConfigurator;
import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentBeanDefinition;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.rabbitmq.RabbitmqExchange;
import io.basc.framework.transform.strategy.filter.ParameterNamePrefixFilter;
import io.basc.framework.util.ClassUtils;

@ConditionalOnParameters
public class RabbitmqBeanDefinitionLoader implements BeanDefinitionLoader {
	public static final String DEFAULT_CONFIG = ResourceUtils.CLASSPATH_URL_PREFIX + "/rabbitmq/rabbitmq.properties";

	public BeanDefinition load(BeanFactory beanFactory, ClassLoader classLoader, String name,
			BeanDefinitionLoaderChain loaderChain) {
		Class<?> sourceClass = ClassUtils.getClass(name, classLoader);
		if (sourceClass == null) {
			return loaderChain.load(beanFactory, classLoader, name);
		}

		if (!beanFactory.isInstance(Environment.class)) {
			return loaderChain.load(beanFactory, classLoader, name);
		}

		ApplicationContext context = beanFactory.getInstance(ApplicationContext.class);
		if (sourceClass == ConnectionFactory.class) {
			return new ConnectionFactoryBeanBuilder(context, sourceClass);
		} else if (sourceClass == Connection.class) {
			return new ConnectionBeanBuilder(beanFactory, sourceClass);
		} else if (Exchange.class == sourceClass || RabbitmqExchange.class == sourceClass) {
			return new ExchangeBeanBuilder(context, sourceClass);
		} else if (sourceClass == ExchangeDeclare.class) {
			return new ExchangeDeclareBeanBuilder(context, sourceClass);
		}
		return loaderChain.load(beanFactory, classLoader, name);
	}

	private static class ConnectionBeanBuilder extends FactoryBeanDefinition {

		public ConnectionBeanBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return getBeanFactory().isInstance(ConnectionFactory.class);
		}

		public Object create() throws BeansException {
			try {
				return getBeanFactory().getInstance(ConnectionFactory.class).newConnection();
			} catch (IOException e) {
				throw new BeansException(e);
			} catch (TimeoutException e) {
				throw new BeansException(e);
			}
		}

		@Override
		public void destroy(Object instance) throws BeansException {
			super.destroy(instance);
			if (instance instanceof Connection) {
				try {
					((Connection) instance).close();
				} catch (IOException e) {
					throw new BeansException(e);
				}
			}
		}
	}

	private static class ConnectionFactoryBeanBuilder extends EnvironmentBeanDefinition {

		public ConnectionFactoryBeanBuilder(Environment environment, Class<?> sourceClass) {
			super(environment, sourceClass);
		}

		public boolean isInstance() {
			return getEnvironment().getResourceLoader().exists(DEFAULT_CONFIG);
		}

		public Object create() throws BeansException {
			ConnectionFactory connectionFactory = new ConnectionFactory();
			Properties properties = getEnvironment().getProperties(DEFAULT_CONFIG).get();
			ConnectionFactoryConfigurator.load(connectionFactory, properties, null);
			ConnectionFactoryConfigurator.load(connectionFactory, properties);
			return connectionFactory;
		}
	}

	private static class ExchangeBeanBuilder extends ContextBeanDefinition {

		public ExchangeBeanBuilder(ApplicationContext context, Class<?> sourceClass) {
			super(context, sourceClass);
		}

		public boolean isInstance() {
			return getBeanFactory().isInstance(Connection.class) && getBeanFactory().isInstance(ExchangeDeclare.class);
		}

		public Object create() throws BeansException {
			return new RabbitmqExchange(getBeanFactory().getInstance(Connection.class),
					getBeanFactory().getInstance(ExchangeDeclare.class));
		}
	}

	private final class ExchangeDeclareBeanBuilder extends ContextBeanDefinition {

		public ExchangeDeclareBeanBuilder(ApplicationContext context, Class<?> sourceClass) {
			super(context, sourceClass);
		}

		@Override
		public boolean isInstance() {
			return getEnvironment().getResourceLoader().exists(DEFAULT_CONFIG);
		}

		@Override
		public Object create() throws BeansException {
			Properties properties = getEnvironment().getProperties(DEFAULT_CONFIG).get();
			ExchangeDeclare exchangeDeclare = new ExchangeDeclare(null);

			ContextConfigurator mapper = new ContextConfigurator(getContext());
			mapper.setConversionService(getEnvironment().getConversionService());
			mapper.configure(getBeanFactory());
			mapper.getFilters().register(new ParameterNamePrefixFilter("exchange"));
			mapper.transform(properties, exchangeDeclare);
			return exchangeDeclare;
		}
	}
}
