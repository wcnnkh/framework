package io.basc.framework.context.config;

import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableApplicationContextSourceLoadExtender<S, T, E extends ApplicationContextSourceLoadExtender<S, T>>
		extends ConfigurableServices<E> implements ApplicationContextSourceLoadExtender<S, T> {

	@Override
	public Elements<T> load(ConfigurableApplicationContext context, S source,
			ApplicationContextSourceLoader<? super S, T> chain) {
		ApplicationContextSourceLoaderChain<S, T> applicationContextSourceLoaderChain = new ApplicationContextSourceLoaderChain<>(
				iterator(), chain);
		return applicationContextSourceLoaderChain.load(context, source);
	}

}
