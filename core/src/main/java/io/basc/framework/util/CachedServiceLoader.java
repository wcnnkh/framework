package io.basc.framework.util;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CachedServiceLoader<S> implements ServiceLoader<S> {
	private final Elements<? extends S> elements;
	private volatile List<S> cache;

	@Override
	public void reload() {
		if (cache != null) {
			synchronized (this) {
				if (cache != null) {
					this.cache = elements.map((e) -> (S) e).toList();
				}
			}
		}
	}

	@Override
	public Elements<S> getServices() {
		if (cache == null) {
			synchronized (this) {
				if (cache == null) {
					this.cache = elements.map((e) -> (S) e).toList();
				}
			}
		}
		return Elements.of(cache);
	}

}
