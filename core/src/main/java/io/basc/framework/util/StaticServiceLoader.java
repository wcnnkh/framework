package io.basc.framework.util;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class StaticServiceLoader<S> extends ElementsWrapper<S, Elements<S>> implements ServiceLoader<S> {

	public StaticServiceLoader(Elements<S> wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public void reload() {
	}

	@Override
	public <U> ServiceLoader<U> map(Function<? super S, ? extends U> mapper) {
		return ServiceLoader.super.map(mapper);
	}

	@Override
	public <U> ServiceLoader<U> flatMap(Function<? super S, ? extends Streamable<U>> mapper) {
		return ServiceLoader.super.flatMap(mapper);
	}

	@Override
	public <U> ServiceLoader<U> convert(Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return ServiceLoader.super.convert(converter);
	}

	@Override
	public ServiceLoader<S> filter(Predicate<? super S> predicate) {
		return ServiceLoader.super.filter(predicate);
	}
}
