package io.basc.framework.env.config;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.env.EnvironmentException;
import io.basc.framework.env.EnvironmentPostProcessor;

public final class EnvironmentPostProcessors extends ConfigurableServices<EnvironmentPostProcessor>
		implements EnvironmentPostProcessor {

	public EnvironmentPostProcessors() {
		super(EnvironmentPostProcessor.class);
	}

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment) {
		for (EnvironmentPostProcessor postProcessor : getServices()) {
			try {
				postProcessor.postProcessEnvironment(environment);
			} catch (Throwable e) {
				throw new EnvironmentException("Post process environment[" + postProcessor + "]", e);
			}
		}
	}
}
