package io.basc.framework.rabbitmq;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConnectionFactoryConfigurator;

import io.basc.framework.amqp.Exchange;
import io.basc.framework.amqp.ExchangeDeclare;
import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanDefinitionLoader;
import io.basc.framework.beans.BeanDefinitionLoaderChain;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.BeanConfigurator;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.io.ResourceUtils;

@Provider
public class RabbitmqBeanDefinitionLoader implements BeanDefinitionLoader {
	public static final String DEFAULT_CONFIG = ResourceUtils.CLASSPATH_URL_PREFIX + "/rabbitmq/rabbitmq.properties";

	public BeanDefinition load(ConfigurableBeanFactory beanFactory, Class<?> sourceClass,
			BeanDefinitionLoaderChain loaderChain) {
		if (sourceClass == ConnectionFactory.class) {
			return new ConnectionFactoryBeanBuilder(beanFactory, sourceClass);
		} else if (sourceClass == Connection.class) {
			return new ConnectionBeanBuilder(beanFactory, sourceClass);
		} else if (Exchange.class == sourceClass || RabbitmqExchange.class == sourceClass) {
			return new ExchangeBeanBuilder(beanFactory, sourceClass);
		} else if (sourceClass == ExchangeDeclare.class) {
			return new ExchangeDeclareBeanBuilder(beanFactory, sourceClass);
		}
		return loaderChain.load(beanFactory, sourceClass);
	}

	private static class ConnectionBeanBuilder extends DefaultBeanDefinition {

		public ConnectionBeanBuilder(ConfigurableBeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return beanFactory.isInstance(ConnectionFactory.class);
		}

		public Object create() throws BeansException {
			try {
				return beanFactory.getInstance(ConnectionFactory.class).newConnection();
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

	private static class ConnectionFactoryBeanBuilder extends DefaultBeanDefinition {

		public ConnectionFactoryBeanBuilder(ConfigurableBeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return beanFactory.getEnvironment().exists(DEFAULT_CONFIG);
		}

		public Object create() throws BeansException {
			ConnectionFactory connectionFactory = new ConnectionFactory();
			Properties properties = beanFactory.getEnvironment().getProperties(DEFAULT_CONFIG).get();
			ConnectionFactoryConfigurator.load(connectionFactory, properties, null);
			ConnectionFactoryConfigurator.load(connectionFactory, properties);
			return connectionFactory;
		}
	}

	private static class ExchangeBeanBuilder extends DefaultBeanDefinition {

		public ExchangeBeanBuilder(ConfigurableBeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return beanFactory.isInstance(Connection.class) && beanFactory.isInstance(ExchangeDeclare.class);
		}

		public Object create() throws BeansException {
			return new RabbitmqExchange(beanFactory.getInstance(Connection.class),
					beanFactory.getInstance(ExchangeDeclare.class));
		}
	}

	private final class ExchangeDeclareBeanBuilder extends DefaultBeanDefinition {

		public ExchangeDeclareBeanBuilder(ConfigurableBeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		@Override
		public boolean isInstance() {
			return beanFactory.getEnvironment().exists(DEFAULT_CONFIG);
		}

		@Override
		public Object create() throws BeansException {
			Properties properties = beanFactory.getEnvironment().getProperties(DEFAULT_CONFIG).get();
			ExchangeDeclare exchangeDeclare = new ExchangeDeclare(null);
			BeanConfigurator mapper = new BeanConfigurator(beanFactory.getEnvironment());
			mapper.setConversionService(beanFactory.getEnvironment().getConversionService());
			mapper.configure(beanFactory);
			mapper.getContext().setNamePrefix("exchange");
			mapper.transform(properties, exchangeDeclare);
			return exchangeDeclare;
		}
	}
}
