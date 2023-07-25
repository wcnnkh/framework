package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.util.spi.Services;

public interface ConfigurableServiceLoaderFactory extends ServiceLoaderFactory {
	@Override
	<S> Services<S> getServiceLoader(Class<S> serviceClass);
}
