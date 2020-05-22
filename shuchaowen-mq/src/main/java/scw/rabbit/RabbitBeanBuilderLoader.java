package scw.rabbit;

import java.util.Properties;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConnectionFactoryConfigurator;

import scw.beans.builder.AbstractBeanBuilder;
import scw.beans.builder.BeanBuilder;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.core.instance.annotation.Configuration;
import scw.io.ResourceUtils;

@Configuration(order = Integer.MIN_VALUE)
public class RabbitBeanBuilderLoader implements BeanBuilderLoader {
	public static final String DEFAULT_CONFIG = ResourceUtils.CLASSPATH_URL_PREFIX + "/rabbitmq/rabbitmq.properties";

	public BeanBuilder loading(LoaderContext context, BeanBuilderLoaderChain loaderChain) {
		if (context.getTargetClass() == ConnectionFactory.class) {
			return new ConnectionFactoryBeanBuilder(context);
		} else if (context.getTargetClass() == Connection.class) {
			return new ConnectionBeanBuilder(context);
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
}
