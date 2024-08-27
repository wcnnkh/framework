package io.basc.framework.util;

import java.util.function.Function;
import java.util.stream.Stream;

public interface ServiceLoaderWrapper<S, W extends Elements<S>> extends ServiceLoader<S>, ElementsWrapper<S, W> {

	@Override
	default <U> ServiceLoader<U> convert(Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return ServiceLoader.super.convert(converter);
	}

	@Override
	default ServiceLoader<S> concat(Elements<? extends S> elements) {
		return ServiceLoader.super.concat(elements);
	}

	@Override
	default Stream<S> stream() {
		return ElementsWrapper.super.stream();
	}
}
