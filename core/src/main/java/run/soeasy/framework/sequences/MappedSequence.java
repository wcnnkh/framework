package run.soeasy.framework.sequences;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapped;
import run.soeasy.framework.core.domain.Wrapper;

@Getter
class MappedSequence<S, T, W extends Sequence<S>> extends Wrapped<W> implements Sequence<T>, Wrapper<W> {
	private final Function<? super S, ? extends T> mapper;

	public MappedSequence(@NonNull W source, Function<? super S, ? extends T> mapper) {
		super(source);
		this.mapper = mapper;
	}

	@Override
	public @NonNull T next() throws UnsupportedOperationException {
		S source = getSource().next();
		return mapper.apply(source);
	}
}
