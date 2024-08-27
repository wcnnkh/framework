package io.basc.framework.util;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ConvertibleElements<S, E> implements ElementsWrapper<E, Elements<E>> {
	@NonNull
	private final Elements<S> source;
	@NonNull
	private final Function<? super Stream<S>, ? extends Stream<E>> converter;

	@Override
	public Elements<E> getSource() {
		return Elements.of(() -> converter.apply(source.stream()));
	}
}
