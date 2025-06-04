package run.soeasy.framework.core.collection;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ConvertedElements<S, E, W extends Elements<S>> implements ElementsWrapper<E, Elements<E>> {
	@NonNull
	private final W target;
	private final boolean resize;
	@NonNull
	private final Function<? super Stream<S>, ? extends Stream<E>> converter;

	@Override
	public Elements<E> getSource() {
		return Elements.of(() -> converter.apply(target.stream()));
	}

	@Override
	public long count() {
		return resize ? ElementsWrapper.super.count() : target.count();
	}

	@Override
	public boolean isEmpty() {
		return resize ? ElementsWrapper.super.isEmpty() : target.isEmpty();
	}

	@Override
	public boolean isUnique() {
		return resize ? ElementsWrapper.super.isUnique() : target.isUnique();
	}
}