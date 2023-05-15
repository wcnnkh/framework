package io.basc.framework.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SharedServiceLoader<S> implements ServiceLoader<S> {
	private final Elements<S> services;

	@Override
	public void reload() {
	}
}
