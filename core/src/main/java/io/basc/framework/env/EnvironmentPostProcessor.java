package io.basc.framework.env;

public interface EnvironmentPostProcessor {
	void postProcessEnvironment(ConfigurableEnvironment environment) throws Throwable;
}
