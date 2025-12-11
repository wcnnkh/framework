package run.soeasy.framework.core.streaming;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TransformedStreamable<E, T> implements Streamable<T> {
	@NonNull
	private final Streamable<E> original;
	@NonNull
	private final Function<? super Stream<E>, ? extends Stream<T>> mapper;

	@Override
	public Stream<T> stream() {
		return mapper.apply(original.stream());
	}

	@Override
	public Streamable<T> reload() {
		Streamable<E> reloadedSource = original.reload();
		if (reloadedSource == original) {
			return this;
		}
		return new TransformedStreamable<>(reloadedSource, this.mapper);
	}
}