package io.basc.framework.context.config;

import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableApplicationContextInitializer extends ConfigurableServices<ApplicationContextInitializer>
		implements ApplicationContextInitializer {

	public ConfigurableApplicationContextInitializer() {
		setServiceClass(ApplicationContextInitializer.class);
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		for (ApplicationContextInitializer initializer : this) {
			initializer.initialize(applicationContext);
		}
	}

}
