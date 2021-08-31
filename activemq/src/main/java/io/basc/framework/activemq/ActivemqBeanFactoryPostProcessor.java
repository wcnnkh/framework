package io.basc.framework.activemq;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.orm.convert.MapToEntityConversionService;

import java.util.Properties;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

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

		public ConnectionFactoryBeanDefinition(ConfigurableBeanFactory beanFactory) {
			super(beanFactory, ActiveMQConnectionFactory.class);
		}

		public boolean isInstance() {
			return beanFactory.getEnvironment().exists(DEFAULT_CONFIG);
		}

		public Object create() throws BeansException {
			Properties properties = beanFactory.getEnvironment().getProperties(DEFAULT_CONFIG).get();
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
			MapToEntityConversionService conversionService = new MapToEntityConversionService();
			conversionService.setConversionService(beanFactory.getEnvironment().getConversionService());
			conversionService.configurationProperties(properties, connectionFactory);
			return connectionFactory;
		}
	}
}