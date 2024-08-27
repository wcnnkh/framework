package io.basc.framework.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StaticServiceLoader<S> implements ServiceLoaderWrapper<S, Elements<S>> {
	@NonNull
	private final Iterable<? extends S> source;

	@Override
	public Elements<S> getSource() {
		return Elements.of(source);
	}

	@Override
	public void reload() {
		if (source instanceof Reloadable) {
			((Reloadable) source).reload();
		}
	}
}
