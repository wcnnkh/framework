package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.observe.service.ObservableServiceLoader;

public interface ConfigurableServiceLoaderFactory extends ServiceLoaderFactory {
	@Override
	<S> ObservableServiceLoader<S> getServiceLoader(Class<S> serviceClass);
}
