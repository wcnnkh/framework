package scw.activemq;

import java.util.Properties;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

import scw.beans.BeanFactory;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;
import scw.convert.support.MapToEntityConversionService;
import scw.io.ResourceUtils;

@Provider
public class ActivemqBeanFactoryPostProcessor implements BeanFactoryPostProcessor{
	private static final String DEFAULT_CONFIG = ResourceUtils.CLASSPATH_URL_PREFIX + "/activemq/activemq.properties";
	
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		ConnectionFactoryBeanDefinition definition = new ConnectionFactoryBeanDefinition(beanFactory);
		if(!beanFactory.containsDefinition(definition.getId())){
			beanFactory.registerDefinition(definition);
			if(!beanFactory.isAlias(ConnectionFactory.class.getName())){
				beanFactory.registerAlias(definition.getId(), ConnectionFactory.class.getName());
			}
		}
	}

	private static class ConnectionFactoryBeanDefinition extends DefaultBeanDefinition {

		public ConnectionFactoryBeanDefinition(BeanFactory beanFactory) {
			super(beanFactory, ActiveMQConnectionFactory.class);
		}

		public boolean isInstance() {
			return beanFactory.getEnvironment().exists(DEFAULT_CONFIG);
		}

		public Object create() throws BeansException {
			Properties properties = beanFactory.getEnvironment().getProperties(DEFAULT_CONFIG).get();
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
			MapToEntityConversionService conversionService = new MapToEntityConversionService(beanFactory.getEnvironment().getConversionService());
			conversionService.configurationProperties(properties, connectionFactory);
			return connectionFactory;
		}
	}
}
