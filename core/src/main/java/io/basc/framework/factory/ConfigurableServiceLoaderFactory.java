package io.basc.framework.factory;

import io.basc.framework.util.GroupedServiceLoader;

public interface ConfigurableServiceLoaderFactory extends ServiceLoaderFactory {
	@Override
	<S> GroupedServiceLoader<S> getServiceLoader(Class<S> serviceClass);
}
