package io.basc.framework.activemq.broker;

import org.apache.activemq.broker.BrokerService;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;

public class ActivemqBrokerApplicationPostProcessor implements ApplicationPostProcessor {

	@Override
	public void postProcessApplication(ConfigurableApplication application) throws Throwable {
		if (enableActivemqBroker(application) && application.isSingleton(BrokerService.class)
				&& application.isInstance(BrokerService.class)) {
			BrokerService service = application.getInstance(BrokerService.class);
			service.start();
		}
	}

	private boolean enableActivemqBroker(Application application) {
		for (Class<?> clazz : application.getSourceClasses()) {
			if (clazz.isAnnotationPresent(EnableActivemqBroker.class)) {
				return true;
			}
		}
		return false;
	}
}
