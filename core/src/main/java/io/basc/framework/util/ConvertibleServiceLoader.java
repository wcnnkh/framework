package io.basc.framework.util;

import java.util.function.Function;
import java.util.stream.Stream;

public class ConvertibleServiceLoader<S, T> extends ConvertibleElements<S, T> implements ServiceLoader<T> {
	private final ServiceLoader<S> serviceLoader;

	public ConvertibleServiceLoader(ServiceLoader<S> serviceLoader,
			Function<? super Stream<S>, ? extends Stream<T>> converter) {
		super(serviceLoader, converter);
		this.serviceLoader = serviceLoader;
	}

	@Override
	public void reload() {
		serviceLoader.reload();
	}
}
