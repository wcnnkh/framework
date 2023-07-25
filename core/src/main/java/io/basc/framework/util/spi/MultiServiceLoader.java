package io.basc.framework.util.spi;

import io.basc.framework.util.element.Elements;

public class MultiServiceLoader<S> implements ServiceLoader<S> {
	private final Elements<? extends ServiceLoader<S>> serviceLoaders;

	public MultiServiceLoader(Elements<? extends ServiceLoader<S>> serviceLoaders) {
		this.serviceLoaders = serviceLoaders;
	}

	@Override
	public void reload() {
		serviceLoaders.forEach(ServiceLoader::reload);
	}

	@Override
	public Elements<S> getServices() {
		return serviceLoaders.flatMap((e) -> e.getServices());
	}
}
