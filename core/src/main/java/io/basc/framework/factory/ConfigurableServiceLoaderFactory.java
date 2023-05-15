package io.basc.framework.factory;

import io.basc.framework.util.ServiceRegistry;

public interface ConfigurableServiceLoaderFactory extends ServiceLoaderFactory {
	@Override
	<S> ServiceRegistry<S> getServiceLoader(Class<S> serviceClass);
}
