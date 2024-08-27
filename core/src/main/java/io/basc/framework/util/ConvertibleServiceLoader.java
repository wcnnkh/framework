package io.basc.framework.util;

import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ConvertibleServiceLoader<S, T> implements ServiceLoaderWrapper<T, Elements<T>> {
	private final ServiceLoader<S> source;
	private final Function<? super Elements<S>, ? extends Elements<T>> converter;

	@Override
	public Elements<T> getSource() {
		return converter.apply(source);
	}

	@Override
	public void reload() {
		source.reload();
	}
}
