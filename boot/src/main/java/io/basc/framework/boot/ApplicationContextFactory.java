package io.basc.framework.boot;

import io.basc.framework.context.config.ConfigurableApplicationContext;

public interface ApplicationContextFactory {
	static final ApplicationContextFactory DEFAULT = new DefaultApplicationContextFactory();

	ConfigurableApplicationContext create(ApplicationType type);
}
