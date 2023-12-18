package io.basc.framework.boot;

import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.context.support.GenericApplicationContext;

class DefaultApplicationContextFactory implements ApplicationContextFactory {

	@Override
	public ConfigurableApplicationContext create(ApplicationType type) {
		return new GenericApplicationContext();
	}

}
