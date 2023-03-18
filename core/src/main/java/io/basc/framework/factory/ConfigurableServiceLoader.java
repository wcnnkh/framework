package io.basc.framework.factory;

import java.util.function.Supplier;

import io.basc.framework.util.Registration;

public interface ConfigurableServiceLoader<S> extends ServiceLoader<S> {
	Registration registerLoader(ServiceLoader<S> serviceLoader);

	Registration register(S service);

	Registration registerSupplier(Supplier<? extends S> serviceSupplier);
}
