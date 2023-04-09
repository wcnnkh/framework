package io.basc.framework.factory;

import io.basc.framework.util.ConfigurableServiceLoader;

public interface ConfigurableServiceLoaderFactory extends ServiceLoaderFactory {
	@Override
	<S> ConfigurableServiceLoader<S> getServiceLoader(Class<S> serviceClass);
}
