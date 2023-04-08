package io.basc.framework.factory;

import io.basc.framework.util.CacheServiceLoader;

public interface ConfigurableServiceLoaderFactory extends ServiceLoaderFactory {
	@Override
	<S> CacheServiceLoader<S> getServiceLoader(Class<S> serviceClass);
}
