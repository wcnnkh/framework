package io.basc.framework.context.config;

import io.basc.framework.beans.factory.config.ConfigurableServices;

public class ConfigurableApplicationContextSourceProcessExtender<E, T extends ApplicationContextSourceProcessExtender<E>>
		extends ConfigurableServices<T> implements ApplicationContextSourceProcessExtender<E> {

	@Override
	public void process(ConfigurableApplicationContext context, E source,
			ApplicationContextSourceProcessor<? super E> chain) {
		ApplicationContextSourceProcessChain<E> applicationContextSourceProcessChain = new ApplicationContextSourceProcessChain<>(
				getServices().iterator(), chain);
		applicationContextSourceProcessChain.process(context, source);
	}
}