package io.basc.framework.netflix.eureka.boot;

import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class EurekaClientApplicationPostProcessor implements ApplicationPostProcessor {
	private static final String AUTO_REGISTRATION = "io.basc.framework.eureka.client.auto.register";

	@Override
	public void postProcessApplication(ConfigurableApplication application) {
		if (enableEurekaClient(application) && application.isInstance(EurekaAutoServiceRegistration.class)) {
			// auto register
			application.getInstance(EurekaAutoServiceRegistration.class);
		}
	}

	private boolean enableEurekaClient(ConfigurableApplication application) {
		Boolean enable = application.getProperties().getAsObject(AUTO_REGISTRATION, Boolean.class);
		if (enable != null) {
			return enable;
		}

		for (Class<?> clazz : application.getSourceClasses().getServices()) {
			EnableEurekaClient enableEurekaClient = clazz.getAnnotation(EnableEurekaClient.class);
			if (enableEurekaClient == null) {
				continue;
			}
			return enableEurekaClient.value();
		}
		return false;
	}
}
