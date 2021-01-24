package scw.rabbitmq;

import java.util.Properties;

import scw.amqp.Exchange;
import scw.amqp.ExchangeDeclare;
import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.BeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.complete.CompleteService;
import scw.configure.support.ConfigureUtils;
import scw.configure.support.MapConfigure;
import scw.context.annotation.Provider;
import scw.io.ResourceUtils;
import scw.io.SerializerUtils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConnectionFactoryConfigurator;

@Provider(order = Integer.MIN_VALUE)
public class RabbitmqBeanBuilderLoader implements BeanDefinitionLoader {
	public static final String DEFAULT_CONFIG = ResourceUtils.CLASSPATH_URL_PREFIX + "/rabbitmq/rabbitmq.properties";

	public BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass, BeanDefinitionLoaderChain loaderChain) {
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

		public ConnectionBeanBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return beanFactory.isInstance(ConnectionFactory.class);
		}

		public Object create() throws Exception {
			return beanFactory.getInstance(ConnectionFactory.class).newConnection();
		}

		@Override
		public void destroy(Object instance) throws Throwable {
			super.destroy(instance);
			if (instance instanceof Connection) {
				((Connection) instance).close();
			}
		}
	}

	private static class ConnectionFactoryBeanBuilder extends DefaultBeanDefinition {

		public ConnectionFactoryBeanBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return ResourceUtils.exists(beanFactory.getEnvironment(), DEFAULT_CONFIG);
		}

		public Object create() throws Exception {
			ConnectionFactory connectionFactory = new ConnectionFactory();
			Properties properties = beanFactory.getEnvironment().getProperties(DEFAULT_CONFIG).get();
			ConnectionFactoryConfigurator.load(connectionFactory, properties, null);
			ConnectionFactoryConfigurator.load(connectionFactory, properties);
			return connectionFactory;
		}
	}

	private static class ExchangeBeanBuilder extends DefaultBeanDefinition {

		public ExchangeBeanBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return beanFactory.isInstance(Connection.class) && beanFactory.isInstance(ExchangeDeclare.class)
					&& beanFactory.isInstance(CompleteService.class);
		}

		public Object create() throws Exception {
			return new RabbitmqExchange(SerializerUtils.DEFAULT_SERIALIZER, beanFactory.getInstance(Connection.class),
					beanFactory.getInstance(ExchangeDeclare.class), true);
		}
	}

	private final class ExchangeDeclareBeanBuilder extends DefaultBeanDefinition {

		public ExchangeDeclareBeanBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		@Override
		public boolean isInstance() {
			return ResourceUtils.exists(beanFactory.getEnvironment(), DEFAULT_CONFIG);
		}

		@Override
		public Object create() throws Exception {
			Properties properties = beanFactory.getEnvironment().getProperties(DEFAULT_CONFIG).get();
			ExchangeDeclare exchangeDeclare = new ExchangeDeclare(null);
			MapConfigure configure = new MapConfigure(ConfigureUtils.getConversionServiceFactory());
			configure.setPrefix("exchange");
			configure.configuration(properties, Properties.class, exchangeDeclare, ExchangeDeclare.class);
			return exchangeDeclare;
		}
	}
}
