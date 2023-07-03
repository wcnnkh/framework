package io.basc.framework.activemq.broker;

import org.apache.activemq.broker.BrokerService;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.Component;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

@Component
public class ActivemqBrokerApplicationPostProcessor implements ApplicationPostProcessor {
	private static Logger logger = LoggerFactory.getLogger(ActivemqBrokerApplicationPostProcessor.class);

	@Override
	public void postProcessApplication(ConfigurableApplication application) throws Throwable {
		if (enableActivemqBroker(application)) {
			for (String name : application.getBeanNamesForType(BrokerService.class)) {
				if (!application.isSingleton(name)) {
					continue;
				}

				BrokerService service = application.getBean(name, BrokerService.class);
				Thread thread = new Thread(() -> {
					try {
						service.start();
					} catch (Exception e) {
						logger.error(e, "Abnormal startup of broker service");
					}
				});
				thread.setDaemon(false);
				thread.setName(service.getBrokerName());
				thread.setContextClassLoader(application.getClassLoader());
				thread.start();
			}
		}
	}

	private boolean enableActivemqBroker(Application application) {
		for (Class<?> clazz : application.getSourceClasses().getServices()) {
			if (clazz.isAnnotationPresent(EnableActivemqBroker.class)) {
				return true;
			}
		}
		return false;
	}
}
