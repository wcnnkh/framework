package scw.activemq;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

import scw.beans.DefaultBeanDefinition;
import scw.beans.BeanDefinition;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.core.instance.annotation.Configuration;
import scw.io.ResourceUtils;
import scw.util.ConfigUtils;

@Configuration(order = Integer.MIN_VALUE)
public class ActivemqBeanBuilderLoader implements BeanBuilderLoader {
	private static final String DEFAULT_CONFIG = ResourceUtils.CLASSPATH_URL_PREFIX + "/activemq/activemq.properties";

	public BeanDefinition loading(LoaderContext context, BeanBuilderLoaderChain loaderChain) {
		if (context.getTargetClass() == ConnectionFactory.class) {
			return new ConnectionFactoryBeanBuilder(context);
		}

		return loaderChain.loading(context);
	}

	private static class ConnectionFactoryBeanBuilder extends DefaultBeanDefinition {
		private final boolean isExist = ResourceUtils.getResourceOperations().isExist(DEFAULT_CONFIG);

		public ConnectionFactoryBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return isExist;
		}

		public Object create() throws Exception {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
			ConfigUtils.loadProperties(connectionFactory,
					ResourceUtils.getResourceOperations().getProperties(DEFAULT_CONFIG).getResource(), null, null);
			return connectionFactory;
		}
	}
}
