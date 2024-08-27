package io.basc.framework.util;

import java.util.function.Function;

public class MergedServiceLoader<S, T extends ServiceLoader<? extends S>>
		implements ServiceLoaderWrapper<S, Elements<S>> {
	private final Elements<ServiceLoader<? extends S>> elements;

	public MergedServiceLoader(Elements<ServiceLoader<? extends S>> elements) {
		this.elements = elements;
	}

	@Override
	public void reload() {
		elements.forEach(ServiceLoader::reload);
	}

	@Override
	public Elements<S> getSource() {
		return elements.flatMap((e) -> e.map(Function.identity()));
	}

	@Override
	public ServiceLoader<S> concat(ServiceLoader<? extends S> serviceLoader) {
		return new MergedServiceLoader<>(this.elements.concat(Elements.singleton(serviceLoader)));
	}
}
