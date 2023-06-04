package io.basc.framework.util;

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
