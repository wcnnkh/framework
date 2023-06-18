package io.basc.framework.boot.config;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.boot.ApplicationException;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;

public final class ApplicationPostProcessors extends ConfigurableServices<ApplicationPostProcessor>
		implements ApplicationPostProcessor {

	public ApplicationPostProcessors() {
		super(ApplicationPostProcessor.class);
	}

	@Override
	public void postProcessApplication(ConfigurableApplication application) {
		for (ApplicationPostProcessor postProcessor : getServices()) {
			try {
				postProcessor.postProcessApplication(application);
			} catch (Throwable e) {
				throw new ApplicationException("Post process application[" + postProcessor + "]", e);
			}
		}
	}

}
