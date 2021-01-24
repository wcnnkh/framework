package scw.activemq;

import java.util.Properties;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.BeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.configure.support.ConfigureUtils;
import scw.context.annotation.Provider;
import scw.convert.TypeDescriptor;
import scw.io.ResourceUtils;

@Provider(order = Integer.MIN_VALUE)
public class ActivemqBeanBuilderLoader implements BeanDefinitionLoader {
	private static final String DEFAULT_CONFIG = ResourceUtils.CLASSPATH_URL_PREFIX + "/activemq/activemq.properties";

	public BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass, BeanDefinitionLoaderChain loaderChain) {
		if (sourceClass == ConnectionFactory.class) {
			return new ConnectionFactoryBeanDefinition(beanFactory, sourceClass);
		}

		return loaderChain.load(beanFactory, sourceClass);
	}

	private static class ConnectionFactoryBeanDefinition extends DefaultBeanDefinition {

		public ConnectionFactoryBeanDefinition(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return beanFactory.getEnvironment().exists(DEFAULT_CONFIG);
		}

		public Object create() throws Exception {
			Properties properties = beanFactory.getEnvironment().getProperties(DEFAULT_CONFIG).get();
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
			ConfigureUtils.getConfigureFactory().configuration(properties, connectionFactory, TypeDescriptor.forObject(connectionFactory));
			return connectionFactory;
		}
	}
}
