package io.basc.framework.netflix.eureka;

import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class EurekaClientApplicationPostProcessor implements ApplicationPostProcessor{

	@Override
	public void postProcessApplication(ConfigurableApplication application) throws Throwable {
		if(application.getBeanFactory().isInstance(EurekaAutoServiceRegistration.class)) {
			//auto register
			application.getBeanFactory().getInstance(EurekaAutoServiceRegistration.class);
		}
	}

}
