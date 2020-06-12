package scw.rabbitmq;

import java.util.Properties;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConnectionFactoryConfigurator;

import scw.amqp.Exchange;
import scw.amqp.ExchangeDeclare;
import scw.beans.builder.AbstractBeanBuilder;
import scw.beans.builder.BeanBuilder;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.complete.CompleteService;
import scw.core.instance.annotation.Configuration;
import scw.io.ResourceUtils;
import scw.io.serialzer.SerializerUtils;
import scw.util.ConfigUtils;

@Configuration(order = Integer.MIN_VALUE)
public class RabbitmqBeanBuilderLoader implements BeanBuilderLoader {
	public static final String DEFAULT_CONFIG = ResourceUtils.CLASSPATH_URL_PREFIX + "/rabbitmq/rabbitmq.properties";

	public BeanBuilder loading(LoaderContext context, BeanBuilderLoaderChain loaderChain) {
		if (context.getTargetClass() == ConnectionFactory.class) {
			return new ConnectionFactoryBeanBuilder(context);
		} else if (context.getTargetClass() == Connection.class) {
			return new ConnectionBeanBuilder(context);
		} else if (Exchange.class == context.getTargetClass()) {
			return new ExchangeBeanBuilder(context);
		} else if (context.getTargetClass() == ExchangeDeclare.class) {
			return new ExchangeDeclareBeanBuilder(context);
		}
		return loaderChain.loading(context);
	}

	private static class ConnectionBeanBuilder extends AbstractBeanBuilder {

		public ConnectionBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return beanFactory.isInstance(ConnectionFactory.class);
		}

		public Object create() throws Exception {
			return beanFactory.getInstance(ConnectionFactory.class).newConnection();
		}

		@Override
		public void destroy(Object instance) throws Exception {
			if (instance instanceof Connection) {
				((Connection) instance).close();
			}
		}
	}

	private static class ConnectionFactoryBeanBuilder extends AbstractBeanBuilder {

		public ConnectionFactoryBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return ResourceUtils.getResourceOperations().isExist(DEFAULT_CONFIG);
		}

		public Object create() throws Exception {
			ConnectionFactory connectionFactory = new ConnectionFactory();
			Properties properties = ResourceUtils.getResourceOperations().getFormattedProperties(DEFAULT_CONFIG,
					propertyFactory);
			ConnectionFactoryConfigurator.load(connectionFactory, properties, null);
			ConnectionFactoryConfigurator.load(connectionFactory, properties);
			return connectionFactory;
		}
	}

	private static class ExchangeBeanBuilder extends AbstractBeanBuilder {

		public ExchangeBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return beanFactory.isInstance(Connection.class) && beanFactory.isInstance(ExchangeDeclare.class)
					&& beanFactory.isInstance(CompleteService.class);
		}

		public Object create() throws Exception {
			return new RabbitmqExchange(SerializerUtils.DEFAULT_SERIALIZER, beanFactory.getInstance(Connection.class),
					beanFactory.getInstance(ExchangeDeclare.class));
		}
	}

	private final class ExchangeDeclareBeanBuilder extends AbstractBeanBuilder {

		public ExchangeDeclareBeanBuilder(LoaderContext context) {
			super(context);
		}

		@Override
		public boolean isInstance() {
			return ResourceUtils.getResourceOperations().isExist(DEFAULT_CONFIG);
		}

		@Override
		public Object create() throws Exception {
			Properties properties = ResourceUtils.getResourceOperations().getFormattedProperties(DEFAULT_CONFIG,
					propertyFactory);
			ExchangeDeclare exchangeDeclare = new ExchangeDeclare(null);
			ConfigUtils.loadProperties(exchangeDeclare, properties, null, "exchange.");
			return exchangeDeclare;
		}
	}
}
