package io.basc.framework.boot;

import io.basc.framework.env.ConfigurableEnvironment;

public interface ConfigurableApplication extends Application {
	ConfigurableEnvironment getEnvironment();
}
