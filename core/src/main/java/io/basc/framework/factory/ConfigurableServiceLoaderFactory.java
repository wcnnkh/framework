package io.basc.framework.factory;

import io.basc.framework.util.ConfigurableServiceLoader1;

public interface ConfigurableServiceLoaderFactory extends ServiceLoaderFactory {
	@Override
	<S> ConfigurableServiceLoader1<S> getServiceLoader(Class<S> serviceClass);
}
