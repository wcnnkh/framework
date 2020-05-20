package scw.mq.support.rabbit;

import java.util.Properties;

import scw.beans.builder.AbstractBeanBuilder;
import scw.beans.builder.BeanBuilder;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.io.ResourceUtils;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConnectionFactoryConfigurator;

public class RabbitBeanBuilderLoader implements BeanBuilderLoader {
	public static final String DEFAULT_CONFIG = ResourceUtils.CLASSPATH_URL_PREFIX
			+ "/rabbitmq/rabbitmq.properties";

	public BeanBuilder loading(LoaderContext context,
			BeanBuilderLoaderChain loaderChain) {
		if (context.getTargetClass() == ConnectionFactory.class) {
			return new ConnectionFactoryBeanBuilder(context);
		}
		return loaderChain.loading(context);
	}

	private static class ConnectionFactoryBeanBuilder extends
			AbstractBeanBuilder {

		public ConnectionFactoryBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return ResourceUtils.getResourceOperations()
					.isExist(DEFAULT_CONFIG);
		}

		public Object create() throws Exception {
			ConnectionFactory connectionFactory = new ConnectionFactory();
			Properties properties = ResourceUtils.getResourceOperations()
					.getFormattedProperties(DEFAULT_CONFIG, propertyFactory);
			ConnectionFactoryConfigurator.load(connectionFactory, properties,
					null);
			ConnectionFactoryConfigurator.load(connectionFactory, properties);
			return connectionFactory;
		}

	}
}
