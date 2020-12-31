package scw.activemq;

import java.util.Properties;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

import scw.beans.BeanDefinition;
import scw.beans.DefaultBeanDefinition;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.configure.support.ConfigureUtils;
import scw.convert.TypeDescriptor;
import scw.core.instance.annotation.SPI;
import scw.io.ResourceUtils;

@SPI(order = Integer.MIN_VALUE)
public class ActivemqBeanBuilderLoader implements BeanBuilderLoader {
	private static final String DEFAULT_CONFIG = ResourceUtils.CLASSPATH_URL_PREFIX + "/activemq/activemq.properties";

	public BeanDefinition loading(LoaderContext context, BeanBuilderLoaderChain loaderChain) {
		if (context.getTargetClass() == ConnectionFactory.class) {
			return new ConnectionFactoryBeanDefinition(context);
		}

		return loaderChain.loading(context);
	}

	private static class ConnectionFactoryBeanDefinition extends DefaultBeanDefinition {
		private final boolean isExist = ResourceUtils.getResourceOperations().isExist(DEFAULT_CONFIG);

		public ConnectionFactoryBeanDefinition(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return isExist;
		}

		public Object create() throws Exception {
			Properties properties = ResourceUtils.getResourceOperations().getProperties(DEFAULT_CONFIG).get();
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
			ConfigureUtils.getConfigureFactory().configuration(properties, connectionFactory, TypeDescriptor.forObject(connectionFactory));
			return connectionFactory;
		}
	}
}
