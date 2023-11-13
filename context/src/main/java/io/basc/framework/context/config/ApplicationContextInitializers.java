package io.basc.framework.context.config;

import io.basc.framework.beans.factory.config.ConfigurableServices;

public class ApplicationContextInitializers extends ConfigurableServices<ApplicationContextInitializer>
		implements ApplicationContextInitializer {

	public ApplicationContextInitializers() {
		super(ApplicationContextInitializer.class);
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		for (ApplicationContextInitializer initializer : getServices()) {
			initializer.initialize(applicationContext);
		}
	}

}
