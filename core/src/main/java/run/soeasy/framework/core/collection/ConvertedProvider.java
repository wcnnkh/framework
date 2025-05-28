package run.soeasy.framework.core.collection;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import lombok.NonNull;

public class ConvertedProvider<S, T, W extends Provider<S>> extends ConvertedElements<S, T, W> implements Provider<T> {

	public ConvertedProvider(@NonNull W target, boolean resize,
			@NonNull Function<? super Stream<S>, ? extends Stream<T>> converter) {
		super(target, resize, converter);
	}

	@Override
	public void reload() {
		getTarget().reload();
	}

	@Override
	public <U> Provider<U> convert(boolean resize, Function<? super Stream<T>, ? extends Stream<U>> converter) {
		return Provider.super.convert(resize, converter);
	}

	@Override
	public Provider<T> concat(Elements<? extends T> elements) {
		return Provider.super.concat(elements);
	}

	@Override
	public Stream<T> stream() {
		return getSource().stream();
	}

	@Override
	public Provider<T> knownSize(@NonNull ToLongFunction<? super Elements<T>> statisticsSize) {
		return Provider.super.knownSize(statisticsSize);
	}

	@Override
	public Provider<T> filter(@NonNull Predicate<? super T> predicate) {
		return Provider.super.filter(predicate);
	}

	@Override
	public <U> Provider<U> map(Function<? super T, ? extends U> mapper) {
		return Provider.super.map(mapper);
	}
}
