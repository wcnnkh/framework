package io.basc.framework.util;

import java.util.function.Supplier;

public interface ConfigurableServiceLoader<S> extends ServiceLoader<S> {
	Registration registerLoader(ServiceLoader<S> serviceLoader);

	Registration register(S service);

	Registration registerSupplier(Supplier<? extends S> serviceSupplier);
}
