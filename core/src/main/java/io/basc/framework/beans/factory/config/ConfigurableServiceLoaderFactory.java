package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.observe.register.ServiceRegistry;

public interface ConfigurableServiceLoaderFactory extends ServiceLoaderFactory {
	@Override
	<S> ServiceRegistry<S> getServiceLoader(Class<S> serviceClass);
}
