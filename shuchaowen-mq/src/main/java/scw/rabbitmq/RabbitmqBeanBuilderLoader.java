package scw.rabbitmq;

import java.util.Properties;

import scw.beans.builder.AbstractBeanBuilder;
import scw.beans.builder.BeanBuilder;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.io.ResourceUtils;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConnectionFactoryConfigurator;

@Configuration(order = Integer.MIN_VALUE)
public class RabbitmqBeanBuilderLoader implements BeanBuilderLoader {
	public static final String DEFAULT_CONFIG = ResourceUtils.CLASSPATH_URL_PREFIX
			+ "/rabbitmq/rabbitmq.properties";

	public BeanBuilder loading(LoaderContext context,
			BeanBuilderLoaderChain loaderChain) {
		if (context.getTargetClass() == ConnectionFactory.class) {
			return new ConnectionFactoryBeanBuilder(context);
		} else if (context.getTargetClass() == Connection.class) {
			return new ConnectionBeanBuilder(context);
		}else if(context.getTargetClass() == RabbitmqExchange.class){
			return new ExchangeBeanBuilder(context); 
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
			return beanFactory.getInstance(ConnectionFactory.class)
					.newConnection();
		}

		@Override
		public void destroy(Object instance) throws Exception {
			if (instance instanceof Connection) {
				((Connection) instance).close();
			}
		}
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

	private static class ExchangeBeanBuilder extends AbstractBeanBuilder {

		public ExchangeBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			if (!beanFactory.isInstance(Connection.class)) {
				return false;
			}

			if (StringUtils.isNotEmpty(getDefaultExchangeName(null))) {
				return true;
			}

			if (ResourceUtils.getResourceOperations().isExist(DEFAULT_CONFIG)) {
				Properties properties = ResourceUtils
						.getResourceOperations()
						.getFormattedProperties(DEFAULT_CONFIG, propertyFactory);
				if (StringUtils.isNotEmpty(getDefaultExchangeName(properties))) {
					return true;
				}
			}
			return false;
		}

		private String getDefaultExchangeName(Properties properties) {
			String value = propertyFactory.getString("rabbitmq.exchange.name");
			if (value == null && properties != null) {
				value = properties.getProperty("exchange.name");
			}
			return value;
		}

		private BuiltinExchangeType getDefaultExchangeType(Properties properties) {
			String value = propertyFactory.getString("rabbitmq.exchange.type");
			if (value == null && properties != null) {
				value = properties.getProperty("exchange.type");
			}
			return StringUtils.isEmpty(value) ? BuiltinExchangeType.DIRECT
					: BuiltinExchangeType.valueOf(value.toUpperCase());
		}
		
		public Object create() throws Exception {
			Properties properties = null;
			if (ResourceUtils.getResourceOperations().isExist(DEFAULT_CONFIG)) {
				properties = ResourceUtils
						.getResourceOperations()
						.getFormattedProperties(DEFAULT_CONFIG, propertyFactory);
			}
			
			String exchangeName = getDefaultExchangeName(properties);
			BuiltinExchangeType type = getDefaultExchangeType(properties);
			return new RabbitmqExchange(beanFactory.getInstance(Connection.class), exchangeName, type, true, false, false, null);
		}
	}
}
