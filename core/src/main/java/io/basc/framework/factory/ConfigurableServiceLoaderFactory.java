package io.basc.framework.factory;

import io.basc.framework.util.Services;

public interface ConfigurableServiceLoaderFactory extends ServiceLoaderFactory {
	@Override
	<S> Services<S> getServiceLoader(Class<S> serviceClass);
}
