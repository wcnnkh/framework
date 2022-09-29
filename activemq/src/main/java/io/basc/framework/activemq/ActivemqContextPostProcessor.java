package io.basc.framework.activemq;

import java.util.Map;
import java.util.Properties;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentPostProcessor;
import io.basc.framework.factory.BeansException;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.orm.support.DefaultObjectRelationalMapper;

@Provider
public class ActivemqContextPostProcessor implements EnvironmentPostProcessor {
	private static final String DEFAULT_CONFIG = ResourceUtils.CLASSPATH_URL_PREFIX + "/activemq/activemq.properties";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment) {
		ConnectionFactoryBeanDefinition definition = new ConnectionFactoryBeanDefinition(environment);
		if (!environment.containsDefinition(definition.getId())) {
			environment.registerDefinition(definition);
			if (!environment.isAlias(ConnectionFactory.class.getName())) {
				environment.registerAlias(definition.getId(), ConnectionFactory.class.getName());
			}
		}
	}

	private static class ConnectionFactoryBeanDefinition extends FactoryBeanDefinition {
		private final Environment environment;

		public ConnectionFactoryBeanDefinition(Environment environment) {
			super(environment, ActiveMQConnectionFactory.class);
			this.environment = environment;
		}

		public boolean isInstance() {
			return environment.getResourceLoader().exists(DEFAULT_CONFIG);
		}

		public Object create() throws BeansException {
			Properties properties = environment.getProperties(DEFAULT_CONFIG).get();
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
			DefaultObjectRelationalMapper mapper = new DefaultObjectRelationalMapper();
			mapper.configure(environment);
			mapper.setConversionService(environment.getConversionService());
			mapper.transform(properties, Map.class, connectionFactory);
			return connectionFactory;
		}
	}
}
