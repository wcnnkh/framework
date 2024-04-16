package io.basc.framework.context.config;

import io.basc.framework.beans.factory.config.ConfigurableServices;

public class ConfigurableApplicationContextInitializer extends ConfigurableServices<ApplicationContextInitializer>
		implements ApplicationContextInitializer {

	public ConfigurableApplicationContextInitializer() {
		super(ApplicationContextInitializer.class);
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		for (ApplicationContextInitializer initializer : getServices()) {
			initializer.initialize(applicationContext);
		}
	}

}
