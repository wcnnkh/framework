package io.basc.framework.factory;

import io.basc.framework.util.Registration;

public interface ConfigurableServiceLoaderFactory extends ServiceLoaderFactory {
	<T> Registration registerService(Class<? extends T> type, T service);
}
