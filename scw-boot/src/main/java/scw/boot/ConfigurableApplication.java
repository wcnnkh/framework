package scw.boot;

import scw.env.ConfigurableEnvironment;

public interface ConfigurableApplication extends Application {
	ConfigurableEnvironment getEnvironment();
}
