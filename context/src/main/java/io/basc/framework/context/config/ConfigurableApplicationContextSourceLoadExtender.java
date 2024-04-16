package io.basc.framework.context.config;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.util.element.Elements;

public class ConfigurableApplicationContextSourceLoadExtender<S, T, E extends ApplicationContextSourceLoadExtender<S, T>>
		extends ConfigurableServices<E> implements ApplicationContextSourceLoadExtender<S, T> {

	@Override
	public Elements<T> load(ConfigurableApplicationContext context, S source,
			ApplicationContextSourceLoader<? super S, T> chain) {
		ApplicationContextSourceLoaderChain<S, T> applicationContextSourceLoaderChain = new ApplicationContextSourceLoaderChain<>(
				getServices().iterator(), chain);
		return applicationContextSourceLoaderChain.load(context, source);
	}

}
