package io.basc.framework.util.spi;

import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class FinalServiceLoader<S> implements ServiceLoader<S> {
	private final Elements<S> services;

	@Override
	public void reload() {
	}
}
