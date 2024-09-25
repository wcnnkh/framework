package io.basc.framework.activemq.client;

import java.util.Map;
import java.util.Properties;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

import io.basc.framework.beans.factory.BeanLifecycleEvent;
import io.basc.framework.beans.factory.BeanLifecycleEvent.Step;
import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.core.Ordered;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.env.EnvironmentPostProcessor;
import io.basc.framework.orm.support.DefaultObjectRelationalMapper;
import io.basc.framework.util.actor.EventListener;

@ConditionalOnParameters(order = Ordered.LOWEST_PRECEDENCE)
public class ActivemqEnvironmentPostProcessor implements EnvironmentPostProcessor, EventListener<BeanLifecycleEvent> {
	private static final String DEFAULT_CONFIG = "activemq/activemq.properties";
	private ConfigurableEnvironment environment;

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment) {
		this.environment = environment;

		if (!environment.isAlias(ConnectionFactory.class.getName())) {
			environment.registerAlias(ActiveMQConnectionFactory.class.getName(), ConnectionFactory.class.getName());
		}

		if (!environment.containsDefinition(ActiveMQConnectionFactory.class.getName())) {
			environment.registerListener(this);
		}
	}

	@Override
	public void onEvent(BeanLifecycleEvent event) {
		if (event.getBean() != null && event.getBean() instanceof ActiveMQConnectionFactory
				&& event.getStep() == Step.AFTER_DEPENDENCE) {
			if (environment.getResourceLoader().exists(DEFAULT_CONFIG)) {
				Properties properties = environment.getProperties(DEFAULT_CONFIG).get();

				ActiveMQConnectionFactory connectionFactory = (ActiveMQConnectionFactory) event.getBean();
				DefaultObjectRelationalMapper mapper = new DefaultObjectRelationalMapper();
				mapper.configure(environment);
				mapper.setConversionService(environment.getConversionService());
				mapper.transform(properties, Map.class, connectionFactory);
			}
		}
	}
}
