package io.basc.framework.util;

import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ConvertibleServiceLoader<S, T> implements ServiceLoader<T> {
	private final ServiceLoader<S> serviceLoader;
	private final Function<? super Elements<S>, ? extends Elements<T>> converter;

	@Override
	public void reload() {
		serviceLoader.reload();
	}

	@Override
	public Elements<T> getServices() {
		Elements<S> services = serviceLoader.getServices();
		return converter.apply(services);
	}
}
